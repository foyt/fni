package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember_;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionEventParticipantDAO extends GenericDAO<IllusionEventParticipant> {

	private static final long serialVersionUID = 1L;

	public IllusionEventParticipant create(User user, IllusionEvent group, String characterName, IllusionEventParticipantRole role) {
		IllusionEventParticipant illusionGroupUser = new IllusionEventParticipant();

    illusionGroupUser.setGroup(group);
    illusionGroupUser.setCharacterName(characterName);
    illusionGroupUser.setRole(role);
    illusionGroupUser.setUser(user);

		return persist(illusionGroupUser);
	}

  public IllusionEventParticipant findByGroupAndUser(IllusionEvent group, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.user), user)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
	public List<IllusionEventParticipant> listByGroup(IllusionEvent group) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
	}
  
  public List<IllusionEventParticipant> listByGroupAndRole(IllusionEvent group, IllusionEventParticipantRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group),
          criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public List<IllusionEventParticipant> listByUserAndRole(User user, IllusionEventParticipantRole role) {
	  EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.user), user),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<IllusionEvent> listIllusionGroupsByUserAndRole(User user, IllusionEventParticipantRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root.get(IllusionGroupMember_.group));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.user), user),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByGroupAndRole(IllusionEvent group, IllusionEventParticipantRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }

	public IllusionEventParticipant updateRole(IllusionEventParticipant illusionEventParticipant, IllusionEventParticipantRole role) {
		illusionEventParticipant.setRole(role);
		return persist(illusionEventParticipant);
	}

  public IllusionEventParticipant updateCharacterName(IllusionEventParticipant illusionEventParticipant, String characterName) {
    illusionEventParticipant.setCharacterName(characterName);
    return persist(illusionEventParticipant);
  }

}

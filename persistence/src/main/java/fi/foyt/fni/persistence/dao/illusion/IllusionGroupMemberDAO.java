package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember_;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionGroupMemberDAO extends GenericDAO<IllusionGroupMember> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupMember create(User user, IllusionGroup group, String characterName, IllusionGroupMemberRole role) {
		IllusionGroupMember illusionGroupUser = new IllusionGroupMember();

    illusionGroupUser.setGroup(group);
    illusionGroupUser.setCharacterName(characterName);
    illusionGroupUser.setRole(role);
    illusionGroupUser.setUser(user);

		return persist(illusionGroupUser);
	}

  public IllusionGroupMember findByGroupAndUser(IllusionGroup group, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMember> criteria = criteriaBuilder.createQuery(IllusionGroupMember.class);
    Root<IllusionGroupMember> root = criteria.from(IllusionGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.user), user)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
	public List<IllusionGroupMember> listByGroup(IllusionGroup group) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMember> criteria = criteriaBuilder.createQuery(IllusionGroupMember.class);
    Root<IllusionGroupMember> root = criteria.from(IllusionGroupMember.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
	}
  
  public List<IllusionGroupMember> listByGroupAndRole(IllusionGroup group, IllusionGroupMemberRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMember> criteria = criteriaBuilder.createQuery(IllusionGroupMember.class);
    Root<IllusionGroupMember> root = criteria.from(IllusionGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group),
          criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public List<IllusionGroupMember> listByUserAndRole(User user, IllusionGroupMemberRole role) {
	  EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMember> criteria = criteriaBuilder.createQuery(IllusionGroupMember.class);
    Root<IllusionGroupMember> root = criteria.from(IllusionGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.user), user),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<IllusionGroup> listIllusionGroupsByUserAndRole(User user, IllusionGroupMemberRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroup> criteria = criteriaBuilder.createQuery(IllusionGroup.class);
    Root<IllusionGroupMember> root = criteria.from(IllusionGroupMember.class);
    criteria.select(root.get(IllusionGroupMember_.group));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.user), user),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByGroupAndRole(IllusionGroup group, IllusionGroupMemberRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<IllusionGroupMember> root = criteria.from(IllusionGroupMember.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMember_.group), group),
        criteriaBuilder.equal(root.get(IllusionGroupMember_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }

	public IllusionGroupMember updateRole(IllusionGroupMember illusionGroupMember, IllusionGroupMemberRole role) {
		illusionGroupMember.setRole(role);
		return persist(illusionGroupMember);
	}

  public IllusionGroupMember updateCharacterName(IllusionGroupMember illusionGroupMember, String characterName) {
    illusionGroupMember.setCharacterName(characterName);
    return persist(illusionGroupMember);
  }

}

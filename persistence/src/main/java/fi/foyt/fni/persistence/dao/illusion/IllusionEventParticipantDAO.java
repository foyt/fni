package fi.foyt.fni.persistence.dao.illusion;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant_;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent_;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionEventParticipantDAO extends GenericDAO<IllusionEventParticipant> {

	private static final long serialVersionUID = 1L;

	public IllusionEventParticipant create(User user, IllusionEvent event, String displayName, IllusionEventParticipantRole role, Date created) {
		IllusionEventParticipant illusionEventParticipant = new IllusionEventParticipant();

    illusionEventParticipant.setEvent(event);
    illusionEventParticipant.setDisplayName(displayName);
    illusionEventParticipant.setRole(role);
    illusionEventParticipant.setUser(user);
    illusionEventParticipant.setCreated(created);

		return persist(illusionEventParticipant);
	}

  public IllusionEventParticipant findByEventAndUser(IllusionEvent event, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.event), event),
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.user), user)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public IllusionEventParticipant findByEventAndAccessCode(IllusionEvent event, String accessCode) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.event), event),
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.accessCode), accessCode)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

	public List<IllusionEventParticipant> listByEvent(IllusionEvent event) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(IllusionEventParticipant_.event), event)
    );

    return entityManager.createQuery(criteria).getResultList();
	}
  
  public List<IllusionEventParticipant> listByEventAndRole(IllusionEvent event, IllusionEventParticipantRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipant> criteria = criteriaBuilder.createQuery(IllusionEventParticipant.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(IllusionEventParticipant_.event), event),
          criteriaBuilder.equal(root.get(IllusionEventParticipant_.role), role)
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
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.user), user),
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<IllusionEvent> listIllusionEventsByUsersAndRole(List<User> users, IllusionEventParticipantRole role, Boolean published) {
    if ((users == null) || (users.isEmpty())) {
      return Collections.emptyList();
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEvent> criteria = criteriaBuilder.createQuery(IllusionEvent.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    Join<IllusionEventParticipant, IllusionEvent> eventJoin = root.join(IllusionEventParticipant_.event);
    
    criteria.select(root.get(IllusionEventParticipant_.event));
    criteria.where(
      criteriaBuilder.and(
        root.get(IllusionEventParticipant_.user).in(users),
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.role), role),
        criteriaBuilder.equal(eventJoin.get(IllusionEvent_.published), published)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByEventAndRole(IllusionEvent event, IllusionEventParticipantRole role) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.event), event),
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Long countByEventAndRoleAndUsers(IllusionEvent event, IllusionEventParticipantRole role,
      List<User> users) {
    
    if ((users == null) || (users.isEmpty())) {
      return 0l;
    }
    
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<IllusionEventParticipant> root = criteria.from(IllusionEventParticipant.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
          root.get(IllusionEventParticipant_.user).in(users),
          criteriaBuilder.equal(root.get(IllusionEventParticipant_.event), event),
        criteriaBuilder.equal(root.get(IllusionEventParticipant_.role), role)
      )
    );

    return entityManager.createQuery(criteria).getSingleResult();
  }


	public IllusionEventParticipant updateRole(IllusionEventParticipant illusionEventParticipant, IllusionEventParticipantRole role) {
		illusionEventParticipant.setRole(role);
		return persist(illusionEventParticipant);
	}

  public IllusionEventParticipant updateDisplayName(IllusionEventParticipant illusionEventParticipant, String displayName) {
    illusionEventParticipant.setDisplayName(displayName);
    return persist(illusionEventParticipant);
  }

  public IllusionEventParticipant updateAccessCode(IllusionEventParticipant illusionEventParticipant, String accessCode) {
    illusionEventParticipant.setAccessCode(accessCode);
    return persist(illusionEventParticipant);
  }

}

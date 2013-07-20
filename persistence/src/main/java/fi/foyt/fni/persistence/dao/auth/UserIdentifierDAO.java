package fi.foyt.fni.persistence.dao.auth;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.auth.UserIdentifier_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class UserIdentifierDAO extends GenericDAO<UserIdentifier> {

	private static final long serialVersionUID = 1L;

	public UserIdentifier create(User user, AuthSource authSource, String sourceId, String identifier) {
    EntityManager entityManager = getEntityManager();

    UserIdentifier userIdentifier = new UserIdentifier();
    userIdentifier.setIdentifier(identifier);
    userIdentifier.setUser(user);
    userIdentifier.setAuthSource(authSource);
    userIdentifier.setSourceId(sourceId);
    
    entityManager.persist(userIdentifier);
    return userIdentifier;
  }

  public UserIdentifier findByAuthSourceAndIdentifier(AuthSource authSource, String identifier) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentifier> criteria = criteriaBuilder.createQuery(UserIdentifier.class);
    Root<UserIdentifier> root = criteria.from(UserIdentifier.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.and(
    		  criteriaBuilder.equal(root.get(UserIdentifier_.identifier), identifier),
    		  criteriaBuilder.equal(root.get(UserIdentifier_.authSource), authSource)
    		)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<UserIdentifier> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentifier> criteria = criteriaBuilder.createQuery(UserIdentifier.class);
    Root<UserIdentifier> root = criteria.from(UserIdentifier.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserIdentifier_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<UserIdentifier> listByAuthSourceAndUser(AuthSource authSource, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserIdentifier> criteria = criteriaBuilder.createQuery(UserIdentifier.class);
    Root<UserIdentifier> root = criteria.from(UserIdentifier.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UserIdentifier_.user), user),
        criteriaBuilder.equal(root.get(UserIdentifier_.authSource), authSource)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }
}

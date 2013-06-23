package fi.foyt.fni.persistence.dao.users;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.users.UserToken_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.UserToken;

@RequestScoped
@DAO
public class UserTokenDAO extends GenericDAO<UserToken> {

	public UserToken create(UserIdentifier userIdentifier, String token, String secret, Date expires, String grantedScopes) {
    EntityManager entityManager = getEntityManager();

    UserToken userToken = new UserToken();
    userToken.setToken(token);
    userToken.setSecret(secret);
    userToken.setExpires(expires);
    userToken.setGrantedScopes(grantedScopes);
    userToken.setUserIdentifier(userIdentifier);
    entityManager.persist(userToken);
    
    return userToken;
  }

  public UserToken findByToken(String token) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserToken> criteria = criteriaBuilder.createQuery(UserToken.class);
    Root<UserToken> root = criteria.from(UserToken.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserToken_.token), token));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public UserToken findByUserIdentifier(UserIdentifier userIdentifier) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserToken> criteria = criteriaBuilder.createQuery(UserToken.class);
    Root<UserToken> root = criteria.from(UserToken.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserToken_.userIdentifier), userIdentifier));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<UserToken> listByUserIdentifier(UserIdentifier userIdentifier) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserToken> criteria = criteriaBuilder.createQuery(UserToken.class);
    Root<UserToken> root = criteria.from(UserToken.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserToken_.userIdentifier), userIdentifier));

    return entityManager.createQuery(criteria).getResultList();
  }
}

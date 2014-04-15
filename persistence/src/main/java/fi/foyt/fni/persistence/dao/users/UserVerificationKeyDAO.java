package fi.foyt.fni.persistence.dao.users;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserVerificationKey;
import fi.foyt.fni.persistence.model.users.UserVerificationKey_;

public class UserVerificationKeyDAO extends GenericDAO<UserVerificationKey> {

	private static final long serialVersionUID = 1L;

	public UserVerificationKey create(User user, String value) {
    EntityManager entityManager = getEntityManager();

    UserVerificationKey userVerificationKey = new UserVerificationKey();
    userVerificationKey.setValue(value);
    userVerificationKey.setUser(user);
    userVerificationKey.setCreated(new Date());
    
    entityManager.persist(userVerificationKey);
    return userVerificationKey;
  }

  public UserVerificationKey findByValue(String value) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserVerificationKey> criteria = criteriaBuilder.createQuery(UserVerificationKey.class);
    Root<UserVerificationKey> root = criteria.from(UserVerificationKey.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserVerificationKey_.value), value));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}

package fi.foyt.fni.persistence.dao.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactField;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.persistence.model.users.UserContactField_;

public class UserContactFieldDAO extends GenericDAO<UserContactField> {

	private static final long serialVersionUID = 1L;

	public UserContactField create(User user, UserContactFieldType type, String value) {
    UserContactField userContactField = new UserContactField();
    userContactField.setUser(user);
    userContactField.setType(type);
    userContactField.setValue(value);
    
    return persist(userContactField);
  }

	public UserContactField findByUserAndType(User user, UserContactFieldType type) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserContactField> criteria = criteriaBuilder.createQuery(UserContactField.class);
    Root<UserContactField> root = criteria.from(UserContactField.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(UserContactField_.user), user),
    		criteriaBuilder.equal(root.get(UserContactField_.type), type)
    	)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

  public List<UserContactField> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserContactField> criteria = criteriaBuilder.createQuery(UserContactField.class);
    Root<UserContactField> root = criteria.from(UserContactField.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserContactField_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }

	public UserContactField updateValue(UserContactField contactField, String value) {
		contactField.setValue(value);
		return persist(contactField);
	}

}

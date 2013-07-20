package fi.foyt.fni.persistence.dao.users;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.PasswordResetKey;
import fi.foyt.fni.persistence.model.users.PasswordResetKey_;

@DAO
public class PasswordResetKeyDAO extends GenericDAO<PasswordResetKey> {

	private static final long serialVersionUID = 1L;

	public PasswordResetKey create(User user, String value) {
    EntityManager entityManager = getEntityManager();

    PasswordResetKey userConfirmKey = new PasswordResetKey();
    userConfirmKey.setValue(value);
    userConfirmKey.setUser(user);
    userConfirmKey.setCreated(new Date());
    
    entityManager.persist(userConfirmKey);
    return userConfirmKey;
  }

  public PasswordResetKey findByValue(String value) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PasswordResetKey> criteria = criteriaBuilder.createQuery(PasswordResetKey.class);
    Root<PasswordResetKey> root = criteria.from(PasswordResetKey.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(PasswordResetKey_.value), value));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}

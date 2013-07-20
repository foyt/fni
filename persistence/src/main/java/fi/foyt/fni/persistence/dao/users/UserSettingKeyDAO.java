package fi.foyt.fni.persistence.dao.users;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.users.UserSettingKey_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.UserSettingKey;

@DAO
public class UserSettingKeyDAO extends GenericDAO<UserSettingKey> {

	private static final long serialVersionUID = 1L;

	public UserSettingKey create(String key) {
    EntityManager entityManager = getEntityManager();

    UserSettingKey userSettingKey = new UserSettingKey();
    userSettingKey.setKey(key);
    entityManager.persist(userSettingKey);
    return userSettingKey;
  }

  public UserSettingKey findByKey(String key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserSettingKey> criteria = criteriaBuilder.createQuery(UserSettingKey.class);
    Root<UserSettingKey> root = criteria.from(UserSettingKey.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserSettingKey_.key), key));

    return getSingleResult(entityManager.createQuery(criteria));
  }
}

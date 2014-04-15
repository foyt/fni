package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserSetting_;

public class IllusionGroupUserSettingDAO extends GenericDAO<IllusionGroupUserSetting> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupUserSetting create(IllusionGroupUser user, IllusionGroupSettingKey key, String value) {
	  IllusionGroupUserSetting illusionGroupUserSetting = new IllusionGroupUserSetting();

    illusionGroupUserSetting.setKey(key);
    illusionGroupUserSetting.setUser(user);
    illusionGroupUserSetting.setValue(value);

		return persist(illusionGroupUserSetting);
	}

  public IllusionGroupUserSetting findByUserAndKey(IllusionGroupUser user, IllusionGroupSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupUserSetting> criteria = criteriaBuilder.createQuery(IllusionGroupUserSetting.class);
    Root<IllusionGroupUserSetting> root = criteria.from(IllusionGroupUserSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupUserSetting_.user), user),
        criteriaBuilder.equal(root.get(IllusionGroupUserSetting_.key), key)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionGroupUserSetting> listByUser(IllusionGroupUser user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupUserSetting> criteria = criteriaBuilder.createQuery(IllusionGroupUserSetting.class);
    Root<IllusionGroupUserSetting> root = criteria.from(IllusionGroupUserSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionGroupUserSetting_.user), user)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionGroupUserSetting updateValue(IllusionGroupUserSetting setting, String value) {
	  setting.setValue(value);
		return persist(setting);
	}

}

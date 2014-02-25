package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSetting_;

@DAO
public class IllusionGroupSettingDAO extends GenericDAO<IllusionGroupSetting> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupSetting create(IllusionGroup group, IllusionGroupSettingKey key, String value) {
	  IllusionGroupSetting illusionGroupUserSetting = new IllusionGroupSetting();

    illusionGroupUserSetting.setKey(key);
    illusionGroupUserSetting.setGroup(group);
    illusionGroupUserSetting.setValue(value);

		return persist(illusionGroupUserSetting);
	}

  public IllusionGroupSetting findByUserAndKey(IllusionGroup group, IllusionGroupSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupSetting> criteria = criteriaBuilder.createQuery(IllusionGroupSetting.class);
    Root<IllusionGroupSetting> root = criteria.from(IllusionGroupSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupSetting_.group), group),
        criteriaBuilder.equal(root.get(IllusionGroupSetting_.key), key)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionGroupSetting> listByUser(IllusionGroup group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupSetting> criteria = criteriaBuilder.createQuery(IllusionGroupSetting.class);
    Root<IllusionGroupSetting> root = criteria.from(IllusionGroupSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionGroupSetting_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionGroupSetting updateValue(IllusionGroupSetting setting, String value) {
	  setting.setValue(value);
		return persist(setting);
	}

}

package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting_;

public class IllusionEventSettingDAO extends GenericDAO<IllusionEventSetting> {

	private static final long serialVersionUID = 1L;

	public IllusionEventSetting create(IllusionEvent group, IllusionEventSettingKey key, String value) {
	  IllusionEventSetting illusionGroupUserSetting = new IllusionEventSetting();

    illusionGroupUserSetting.setKey(key);
    illusionGroupUserSetting.setGroup(group);
    illusionGroupUserSetting.setValue(value);

		return persist(illusionGroupUserSetting);
	}

  public IllusionEventSetting findByUserAndKey(IllusionEvent group, IllusionEventSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventSetting> criteria = criteriaBuilder.createQuery(IllusionEventSetting.class);
    Root<IllusionEventSetting> root = criteria.from(IllusionEventSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventSetting_.group), group),
        criteriaBuilder.equal(root.get(IllusionEventSetting_.key), key)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventSetting> listByGroup(IllusionEvent group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventSetting> criteria = criteriaBuilder.createQuery(IllusionEventSetting.class);
    Root<IllusionEventSetting> root = criteria.from(IllusionEventSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventSetting_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionEventSetting updateValue(IllusionEventSetting setting, String value) {
	  setting.setValue(value);
		return persist(setting);
	}

}

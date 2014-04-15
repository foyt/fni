package fi.foyt.fni.persistence.dao.system;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.system.SystemSetting;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.system.SystemSetting_;

public class SystemSettingDAO extends GenericDAO<SystemSetting> {
	
	private static final long serialVersionUID = 1L;

	public SystemSetting create(SystemSettingKey key, String value) {
    EntityManager entityManager = getEntityManager();

    SystemSetting systemSetting = new SystemSetting();
    systemSetting.setKey(key);
    systemSetting.setValue(value);
    
    entityManager.persist(systemSetting);

    return systemSetting;
  }

  public SystemSetting findByKey(SystemSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<SystemSetting> criteria = criteriaBuilder.createQuery(SystemSetting.class);
    Root<SystemSetting> root = criteria.from(SystemSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(SystemSetting_.key), key)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public SystemSetting updateValue(SystemSetting systemSetting, String value) {
    EntityManager entityManager = getEntityManager();
    systemSetting.setValue(value);
    
    systemSetting = entityManager.merge(systemSetting);
    return systemSetting;
  }
}

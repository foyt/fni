package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.MaterialRevision;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionSetting_;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey;

@DAO
public class MaterialRevisionSettingDAO extends GenericDAO<MaterialRevisionSetting> {

	private static final long serialVersionUID = 1L;

	public MaterialRevisionSetting create(MaterialRevision materialRevision, MaterialSettingKey key, String value) {
    EntityManager entityManager = getEntityManager();

    MaterialRevisionSetting materialRevisionSetting = new MaterialRevisionSetting();
    materialRevisionSetting.setMaterialRevision(materialRevision);
    materialRevisionSetting.setKey(key);
    materialRevisionSetting.setValue(value);
    
    entityManager.persist(materialRevisionSetting);

    return materialRevisionSetting;
  }
	
  public MaterialRevisionSetting findByMaterialRevisionAndKey(MaterialRevision materialRevision, MaterialSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialRevisionSetting> criteria = criteriaBuilder.createQuery(MaterialRevisionSetting.class);
    Root<MaterialRevisionSetting> root = criteria.from(MaterialRevisionSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(MaterialRevisionSetting_.materialRevision), materialRevision),
        criteriaBuilder.equal(root.get(MaterialRevisionSetting_.key), key)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
	
  public List<MaterialRevisionSetting> listByMaterialRevision(MaterialRevision materialRevision) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialRevisionSetting> criteria = criteriaBuilder.createQuery(MaterialRevisionSetting.class);
    Root<MaterialRevisionSetting> root = criteria.from(MaterialRevisionSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialRevisionSetting_.materialRevision), materialRevision)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
	
}

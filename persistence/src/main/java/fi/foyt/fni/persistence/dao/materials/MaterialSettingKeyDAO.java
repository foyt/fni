package fi.foyt.fni.persistence.dao.materials;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey;
import fi.foyt.fni.persistence.model.materials.MaterialSettingKey_;

public class MaterialSettingKeyDAO extends GenericDAO<MaterialSettingKey> {

	private static final long serialVersionUID = 1L;

	public MaterialSettingKey create(String name) {
    EntityManager entityManager = getEntityManager();

    MaterialSettingKey materialSettingKey = new MaterialSettingKey();
    materialSettingKey.setName(name);

    entityManager.persist(materialSettingKey);

    return materialSettingKey;
  }

  public MaterialSettingKey findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialSettingKey> criteria = criteriaBuilder.createQuery(MaterialSettingKey.class);
    Root<MaterialSettingKey> root = criteria.from(MaterialSettingKey.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(MaterialSettingKey_.name), name));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}

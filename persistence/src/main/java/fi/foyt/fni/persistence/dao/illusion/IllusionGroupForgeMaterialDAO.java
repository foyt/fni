package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupForgeMaterial;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupForgeMaterial_;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMaterialType;
import fi.foyt.fni.persistence.model.materials.Material;

public class IllusionGroupForgeMaterialDAO extends GenericDAO<IllusionGroupForgeMaterial> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupForgeMaterial create(IllusionGroup group, IllusionGroupMaterialType type, Material forgeMaterial) {
		IllusionGroupForgeMaterial illusionGroupForgeMaterial = new IllusionGroupForgeMaterial();
		
    illusionGroupForgeMaterial.setGroup(group);
    illusionGroupForgeMaterial.setForgeMaterial(forgeMaterial);
    illusionGroupForgeMaterial.setType(type);
    
		return persist(illusionGroupForgeMaterial);
	}
  
	public List<IllusionGroupForgeMaterial> listByGroup(IllusionGroup group) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupForgeMaterial> criteria = criteriaBuilder.createQuery(IllusionGroupForgeMaterial.class);
    Root<IllusionGroupForgeMaterial> root = criteria.from(IllusionGroupForgeMaterial.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(IllusionGroupForgeMaterial_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
	}
  
  public List<IllusionGroupForgeMaterial> listByGroupAndType(IllusionGroup group, IllusionGroupMaterialType type) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupForgeMaterial> criteria = criteriaBuilder.createQuery(IllusionGroupForgeMaterial.class);
    Root<IllusionGroupForgeMaterial> root = criteria.from(IllusionGroupForgeMaterial.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(IllusionGroupForgeMaterial_.group), group),
          criteriaBuilder.equal(root.get(IllusionGroupForgeMaterial_.type), type)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

}

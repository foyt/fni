package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.MaterialTag_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialTag;

@RequestScoped
@DAO
public class MaterialTagDAO extends GenericDAO<MaterialTag> {

	public MaterialTag create(Material material, Tag tag) {
    EntityManager entityManager = getEntityManager();

    MaterialTag materialTag = new MaterialTag();
    materialTag.setMaterial(material);
    materialTag.setTag(tag);
    
    entityManager.persist(materialTag);

    return materialTag;
  }

  public List<MaterialTag> listByMaterial(Material material) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialTag> criteria = criteriaBuilder.createQuery(MaterialTag.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialTag_.material), material)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public MaterialTag findByMaterialAndTag(Material material, Tag tag) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialTag> criteria = criteriaBuilder.createQuery(MaterialTag.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(MaterialTag_.material), material),
        criteriaBuilder.equal(root.get(MaterialTag_.tag), tag)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  

}

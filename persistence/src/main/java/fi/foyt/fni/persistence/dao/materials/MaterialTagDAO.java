package fi.foyt.fni.persistence.dao.materials;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.MaterialTag;
import fi.foyt.fni.persistence.model.materials.MaterialTag_;
import fi.foyt.fni.persistence.model.materials.Material_;

public class MaterialTagDAO extends GenericDAO<MaterialTag> {

	private static final long serialVersionUID = 1L;

	public MaterialTag create(Material material, Tag tag) {
    EntityManager entityManager = getEntityManager();

    MaterialTag materialTag = new MaterialTag();
    materialTag.setMaterial(material);
    materialTag.setTag(tag);
    
    entityManager.persist(materialTag);

    return materialTag;
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

	public List<MaterialTag> listByMaterialAndTagsNotIn(Material material, List<Tag> tags) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialTag> criteria = criteriaBuilder.createQuery(MaterialTag.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialTag_.material), material),
      criteriaBuilder.not(root.get(MaterialTag_.tag).in(tags))
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

  public List<Material> listMaterialsByPublicityAndTags(MaterialPublicity publicity, List<Tag> tags) {
    if ((tags == null) || (tags.isEmpty())) {
      return Collections.emptyList();
    }
     
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Material> criteria = criteriaBuilder.createQuery(Material.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    criteria.select(root.get(MaterialTag_.material)).distinct(true);
    Join<MaterialTag, Material> materialJoin = root.join(MaterialTag_.material);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(materialJoin.get(Material_.publicity), publicity),
        root.get(MaterialTag_.tag).in(tags)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByTag(Tag tag) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialTag_.tag), tag)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public Long countByTagAndMaterialPublicity(Tag tag, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    Join<MaterialTag, Material> materialJoin = root.join(MaterialTag_.material);
    
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(MaterialTag_.tag), tag),
        criteriaBuilder.equal(materialJoin.get(Material_.publicity), publicity)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public List<TagWithCount> listWithCountsByMaterialPublicityOrderByCountAndName(MaterialPublicity publicity, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<TagWithCount> criteria = criteriaBuilder.createQuery(TagWithCount.class);
    Root<MaterialTag> root = criteria.from(MaterialTag.class);
    Join<MaterialTag, Material> materialJoin = root.join(MaterialTag_.material);
    criteria.multiselect(root.get(MaterialTag_.tag), criteriaBuilder.count(root.get(MaterialTag_.material)));
    
    criteria.where(
      criteriaBuilder.equal(materialJoin.get(Material_.publicity), publicity)
    );
    
    criteria.groupBy(root.get(MaterialTag_.tag));
    criteria.orderBy(
      criteriaBuilder.desc(criteriaBuilder.count(root.get(MaterialTag_.material))),
      criteriaBuilder.desc(criteriaBuilder.count(materialJoin.get(Material_.title)))
    );
    
    TypedQuery<TagWithCount> query = entityManager.createQuery(criteria);
    
    if (firstResult != null) {
      query.setFirstResult(firstResult);
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults);
    }
    
    return query.getResultList();
  }

}

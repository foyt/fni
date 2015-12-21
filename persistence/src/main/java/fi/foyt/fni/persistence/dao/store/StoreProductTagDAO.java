package fi.foyt.fni.persistence.dao.store;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.store.StoreProductTag;
import fi.foyt.fni.persistence.model.store.StoreProductTag_;
import fi.foyt.fni.persistence.model.store.StoreProduct_;
import fi.foyt.fni.persistence.model.store.StoreTag;

public class StoreProductTagDAO extends GenericDAO<StoreProductTag> {
  
	private static final long serialVersionUID = 1L;

	public StoreProductTag create(StoreTag tag, StoreProduct product) {
		StoreProductTag storeProductTag = new StoreProductTag();
		storeProductTag.setProduct(product);
		storeProductTag.setTag(tag);
		return persist(storeProductTag);
	}

	public StoreProductTag findByProductAndTag(StoreProduct product, StoreTag tag) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreProductTag> criteria = criteriaBuilder.createQuery(StoreProductTag.class);
    Root<StoreProductTag> root = criteria.from(StoreProductTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(StoreProductTag_.product), product),
      criteriaBuilder.equal(root.get(StoreProductTag_.tag), tag)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
	}
	
	public List<StoreProductTag> listByProduct(StoreProduct product) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreProductTag> criteria = criteriaBuilder.createQuery(StoreProductTag.class);
    Root<StoreProductTag> root = criteria.from(StoreProductTag.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(StoreProductTag_.product), product)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<StoreProduct> listProductsByStoreTags(List<StoreTag> storeTags) {
	  if (storeTags.isEmpty()) {
	    return Collections.emptyList();
	  }
	  
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreProduct> criteria = criteriaBuilder.createQuery(StoreProduct.class);
    Root<StoreProductTag> root = criteria.from(StoreProductTag.class);
    criteria.select(root.get(StoreProductTag_.product));
    criteria.where(
    	root.get(StoreProductTag_.tag).in(storeTags)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public Long countProductsByTag(StoreTag tag) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<StoreProductTag> root = criteria.from(StoreProductTag.class);
    criteria.select(criteriaBuilder.count(root.get(StoreProductTag_.product)));
    criteria.where(
    		criteriaBuilder.equal(root.get(StoreProductTag_.tag), tag)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
	}

	public List<StoreTag> listStoreTagsByProductPublished(Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreTag> criteria = criteriaBuilder.createQuery(StoreTag.class);
    Root<StoreProductTag> root = criteria.from(StoreProductTag.class);
    Join<StoreProductTag, StoreProduct> join = root.join(StoreProductTag_.product);
    criteria.select(root.get(StoreProductTag_.tag)).distinct(true);
    
    criteria.where(
    		criteriaBuilder.and(
    		  criteriaBuilder.equal(join.get(StoreProduct_.published), published)
    		)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

}

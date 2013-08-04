package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Product;
import fi.foyt.fni.persistence.model.gamelibrary.ProductTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.ProductTag_;
import fi.foyt.fni.persistence.model.gamelibrary.Product_;

@DAO
public class ProductTagDAO extends GenericDAO<ProductTag> {
  
	private static final long serialVersionUID = 1L;

	public ProductTag create(GameLibraryTag tag, Product product) {
		ProductTag productTag = new ProductTag();
		productTag.setProduct(product);
		productTag.setTag(tag);
		getEntityManager().persist(productTag);
		return productTag;
	}

	public List<ProductTag> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductTag> criteria = criteriaBuilder.createQuery(ProductTag.class);
    Root<ProductTag> root = criteria.from(ProductTag.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(ProductTag_.product), product)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<Product> listProductsByGameLibraryTags(List<GameLibraryTag> gameLibaryTags) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<ProductTag> root = criteria.from(ProductTag.class);
    criteria.select(root.get(ProductTag_.product));
    criteria.where(
    	root.get(ProductTag_.tag).in(gameLibaryTags)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public Long countProductsByTag(GameLibraryTag tag) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ProductTag> root = criteria.from(ProductTag.class);
    criteria.select(criteriaBuilder.count(root.get(ProductTag_.product)));
    criteria.where(
    		criteriaBuilder.equal(root.get(ProductTag_.tag), tag)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
	}

	public List<GameLibraryTag> listGameLibraryTagsByProductPublished(Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GameLibraryTag> criteria = criteriaBuilder.createQuery(GameLibraryTag.class);
    Root<ProductTag> root = criteria.from(ProductTag.class);
    Join<ProductTag, Product> join = root.join(ProductTag_.product);
    criteria.select(root.get(ProductTag_.tag)).distinct(true);
    
    criteria.where(
    		criteriaBuilder.and(
    		  criteriaBuilder.equal(join.get(Product_.published), published)
    		)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
}

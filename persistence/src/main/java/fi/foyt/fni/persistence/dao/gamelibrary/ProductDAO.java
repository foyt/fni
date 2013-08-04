package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Product;
import fi.foyt.fni.persistence.model.gamelibrary.ProductImage;
import fi.foyt.fni.persistence.model.gamelibrary.Product_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ProductDAO extends GenericDAO<Product> {
  
	private static final long serialVersionUID = 1L;

	public Product findByUrlName(String urlName) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<Product> root = criteria.from(Product.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Product_.urlName), urlName)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
	}

	public List<Product> listByPublishedOrderByCreated(Boolean published, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<Product> root = criteria.from(Product.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Product_.published), published)
    );
    
    
    criteria.orderBy(criteriaBuilder.desc(root.get(Product_.created)));

    TypedQuery<Product> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

	public List<Product> listByPublished(Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<Product> root = criteria.from(Product.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Product_.published), published)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public List<Product> listByCreatorAndPublished(User creator, Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<Product> root = criteria.from(Product.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.and(
  		  criteriaBuilder.equal(root.get(Product_.creator), creator),
  			criteriaBuilder.equal(root.get(Product_.published), published)
  		)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
	
	public Product updateName(Product product, String name) {
		product.setName(name);
		getEntityManager().persist(product);
		return product;
	}

	public Product updateDescription(Product product, String description) {
		product.setDescription(description);
		getEntityManager().persist(product);
		return product;
	}

	public Product updateDefaultImage(Product product, ProductImage defaultImage) {
		product.setDefaultImage(defaultImage);
		getEntityManager().persist(product);
		return product;
	}

	public Product updatePrice(Product product, Double price) {
		product.setPrice(price);
		getEntityManager().persist(product);
		return product;
	}

	public Product updatePublished(Product product, Boolean published) {
		product.setPublished(published);
		getEntityManager().persist(product);
		return product;
	}

	public Product updateModified(Product product, Date modified) {
		product.setModified(modified);
		getEntityManager().persist(product);
		return product;
	}
	
	public Product updateModifier(Product product, User modifier) {
		product.setModifier(modifier);
		getEntityManager().persist(product);
		return product;
	}

	public Product updateRequiresDelivery(Product product, Boolean requiresDelivery) {
		product.setRequiresDelivery(requiresDelivery);
		getEntityManager().persist(product);
		return product;
	}

	public Product updateWidth(Product product, Integer width) {
		product.setWidth(width);
		getEntityManager().persist(product);
    return product;
	}

	public Product updateHeight(Product product, Integer height) {
		product.setHeight(height);
		getEntityManager().persist(product);
    return product;
	}

	public Product updateDepth(Product product, Integer depth) {
		product.setDepth(depth);
		getEntityManager().persist(product);
    return product;
	}

	public Product updateWeight(Product product, Double weight) {
		product.setWeight(weight);
		getEntityManager().persist(product);
    return product;
	}

	public Product updatePurchasable(Product product, Boolean purchasable) {
		product.setPurchasable(purchasable);
		getEntityManager().persist(product);
    return product;
	}

}

package fi.foyt.fni.persistence.dao.store;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.Product_;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class ProductDAO extends GenericDAO<Product> {
  
	public List<Product> listAllOrderByCreated(int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<Product> root = criteria.from(Product.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(Product_.created)));

    TypedQuery<Product> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
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
}

package fi.foyt.fni.persistence.dao.store;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.ProductTag_;
import fi.foyt.fni.persistence.model.store.StoreTag;

@RequestScoped
@DAO
public class ProductTagDAO extends GenericDAO<ProductTag> {
  
	public ProductTag create(StoreTag tag, Product product) {
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

	public List<Product> listProductsByStoreTags(List<StoreTag> storeTags) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> criteria = criteriaBuilder.createQuery(Product.class);
    Root<ProductTag> root = criteria.from(ProductTag.class);
    criteria.select(root.get(ProductTag_.product));
    criteria.where(
    	root.get(ProductTag_.tag).in(storeTags)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
}

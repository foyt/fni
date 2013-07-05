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
import fi.foyt.fni.persistence.model.store.ProductDetail;
import fi.foyt.fni.persistence.model.store.ProductDetail_;
import fi.foyt.fni.persistence.model.store.StoreDetail;

@RequestScoped
@DAO
public class ProductDetailDAO extends GenericDAO<ProductDetail> {
  
	public ProductDetail create(StoreDetail detail, Product product, String value) {
		ProductDetail productDetail = new ProductDetail();
		productDetail.setProduct(product);
		productDetail.setDetail(detail);
		productDetail.setValue(value);
		getEntityManager().persist(productDetail);
		return productDetail;
	}

	public ProductDetail findByProductAndDetail(Product product, StoreDetail storeDetail) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductDetail> criteria = criteriaBuilder.createQuery(ProductDetail.class);
    Root<ProductDetail> root = criteria.from(ProductDetail.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(ProductDetail_.product), product),
    		criteriaBuilder.equal(root.get(ProductDetail_.detail), storeDetail)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
	}

	public List<ProductDetail> listByProduct(Product product) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductDetail> criteria = criteriaBuilder.createQuery(ProductDetail.class);
    Root<ProductDetail> root = criteria.from(ProductDetail.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(ProductDetail_.product), product)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public ProductDetail updateValue(ProductDetail productDetail, String value) {
		productDetail.setValue(value);
		getEntityManager().persist(productDetail);
		return productDetail;
	}
	
}

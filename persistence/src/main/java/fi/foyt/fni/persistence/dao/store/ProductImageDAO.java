package fi.foyt.fni.persistence.dao.store;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.ProductImage_;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class ProductImageDAO extends GenericDAO<ProductImage> {
  
	public ProductImage create(Product product, byte[] content, String contentType, Date created, Date modified, User creator, User modifier) {
		ProductImage productImage = new ProductImage();
		productImage.setContent(content);
		productImage.setContentType(contentType);
		productImage.setCreated(created);
		productImage.setCreator(creator);
		productImage.setModified(modified);
		productImage.setModifier(modifier);
		productImage.setProduct(product);
		
		getEntityManager().persist(productImage);
		
		return productImage;
	}

	public List<ProductImage> listByProduct(Product product) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ProductImage> criteria = criteriaBuilder.createQuery(ProductImage.class);
    Root<ProductImage> root = criteria.from(ProductImage.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(ProductImage_.product), product)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
	
}

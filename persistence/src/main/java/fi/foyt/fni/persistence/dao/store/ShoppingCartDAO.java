package fi.foyt.fni.persistence.dao.store;

import java.util.Date;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.Address;
import fi.foyt.fni.persistence.model.store.DeliveryMethod;
import fi.foyt.fni.persistence.model.store.PaymentMethod;
import fi.foyt.fni.persistence.model.store.ShoppingCart;
import fi.foyt.fni.persistence.model.store.ShoppingCart_;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class ShoppingCartDAO extends GenericDAO<ShoppingCart> {
  
	public ShoppingCart create(User customer, String sessionId, DeliveryMethod deliveryMethod, PaymentMethod paymentMethod, Address deliveryAddress, Date created, Date modified) {
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setDeliveryMethod(deliveryMethod);
		shoppingCart.setCreated(created);
		shoppingCart.setCustomer(customer);
		shoppingCart.setSessionId(sessionId);
		shoppingCart.setDeliveryAddress(deliveryAddress);
		shoppingCart.setModified(modified);
		shoppingCart.setPaymentMethod(paymentMethod);
		
		getEntityManager().persist(shoppingCart);
		
		return shoppingCart;
	}

	public ShoppingCart findByCustomer(User customer) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ShoppingCart> criteria = criteriaBuilder.createQuery(ShoppingCart.class);
    Root<ShoppingCart> root = criteria.from(ShoppingCart.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(ShoppingCart_.customer), customer)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
	}

	public ShoppingCart updateCustomer(ShoppingCart shoppingCart, User customer) {
		shoppingCart.setCustomer(customer);
		getEntityManager().persist(shoppingCart);
		return shoppingCart;
	}
	
}

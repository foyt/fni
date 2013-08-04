package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Address;
import fi.foyt.fni.persistence.model.gamelibrary.PaymentMethod;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCart;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCart_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ShoppingCartDAO extends GenericDAO<ShoppingCart> {
  
	private static final long serialVersionUID = 1L;

	public ShoppingCart create(User customer, String sessionId, String deliveryMethodId, PaymentMethod paymentMethod, Address deliveryAddress, Date created, Date modified) {
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setDeliveryMethodId(deliveryMethodId);
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

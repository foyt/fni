package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.Order_;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.User;

public class OrderDAO extends GenericDAO<Order> {

	private static final long serialVersionUID = 1L;

	public Order create(User customer, String accessKey, String customerCompany, String customerEmail, String customerFirstName, String customerLastName, String customerMobile,
			String customerPhone, OrderStatus orderStatus, Double shippingCosts, String notes, Address deliveryAddress, Date created, Date canceled, Date paid,
			Date shipped, Date delivered) {
		
	  Order order = new Order();

		order.setAccessKey(accessKey);
		order.setCanceled(canceled);
		order.setCreated(created);
		order.setCustomer(customer);
		order.setCustomerCompany(customerCompany);
		order.setCustomerEmail(customerEmail);
		order.setCustomerFirstName(customerFirstName);
		order.setCustomerLastName(customerLastName);
		order.setCustomerMobile(customerMobile);
		order.setCustomerPhone(customerPhone);
		order.setShipped(shipped);
		order.setDelivered(delivered);
		order.setDeliveryAddress(deliveryAddress);
		order.setNotes(notes);
		order.setOrderStatus(orderStatus);
		order.setPaid(paid);
		order.setShippingCosts(shippingCosts);

		return persist(order);
	}
	
	public List<Order> listByOrderStatus(OrderStatus orderStatus) {
	  EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Order> criteria = criteriaBuilder.createQuery(Order.class);
    Root<Order> root = criteria.from(Order.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Order_.orderStatus), orderStatus));

    return entityManager.createQuery(criteria).getResultList();
	}

	public Order updateOrderStatus(Order order, OrderStatus orderStatus) {
		order.setOrderStatus(orderStatus);
		return persist(order);
	}

	public Order updateShipped(Order order, Date shipped) {
		order.setShipped(shipped);
		return persist(order);
	}

	public Order updatePaid(Order order, Date paid) {
		order.setPaid(paid);
		return persist(order);
	}

	public Order updateCanceled(Order order, Date canceled) {
	  order.setCanceled(canceled);
    return persist(order);
	}

  public Order updateDelivered(Order order, Date delivered) {
    order.setDelivered(delivered);
    return persist(order);
  }

}

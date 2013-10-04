package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class OrderDAO extends GenericDAO<Order> {

	private static final long serialVersionUID = 1L;

	public Order create(User customer, String customerCompany, String customerEmail, String customerFirstName, String customerLastName, String customerMobile,
			String customerPhone, OrderStatus orderStatus, Double shippingCosts, String notes, Address deliveryAddress, Date created, Date canceled, Date paid,
			Date shipped, Date delivered) {
		Order order = new Order();

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

}

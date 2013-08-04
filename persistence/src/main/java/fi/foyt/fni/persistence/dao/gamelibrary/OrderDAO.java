package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Address;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.PaymentMethod;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class OrderDAO extends GenericDAO<Order> {
  
	private static final long serialVersionUID = 1L;

	public Order create(User customer, OrderStatus orderStatus, PaymentMethod paymentMethod, Double shippingCosts, String notes, Address deliveryAddress, Date created, Date canceled, Date checkedOut, Date paid, Date delivered) {
		Order order = new Order();
		
		order.setCanceled(canceled);
		order.setCheckedOut(checkedOut);
		order.setCreated(created);
		order.setCustomer(customer);
		order.setDelivered(delivered);
		order.setDeliveryAddress(deliveryAddress);
		order.setNotes(notes);
		order.setOrderStatus(orderStatus);
		order.setPaid(paid);
		order.setPaymentMethod(paymentMethod);
		order.setShippingCosts(shippingCosts);
		
		getEntityManager().persist(order);
		
		return order;
	}
	
}

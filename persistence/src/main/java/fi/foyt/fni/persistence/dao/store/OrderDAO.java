package fi.foyt.fni.persistence.dao.store;

import java.util.Date;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.Address;
import fi.foyt.fni.persistence.model.store.Order;
import fi.foyt.fni.persistence.model.store.OrderStatus;
import fi.foyt.fni.persistence.model.store.PaymentMethod;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class OrderDAO extends GenericDAO<Order> {
  
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

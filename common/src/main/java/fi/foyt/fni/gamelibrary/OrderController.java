package fi.foyt.fni.gamelibrary;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.gamelibrary.OrderDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.OrderItemDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.PaymentMethod;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class OrderController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private OrderDAO orderDAO;
	
	@Inject
	private OrderItemDAO orderItemDAO;
	
	/* Order */
	
	public Order createOrder(User customer, String customerCompany, String customerEmail, String customerFirstName, String customerLastName, String customerMobile, String customerPhone, OrderStatus orderStatus, PaymentMethod paymentMethod, Double shippingCosts, String notes, Address deliveryAddress) {
		Date now = new Date();
		return orderDAO.create(customer, customerCompany, customerEmail, customerFirstName, customerLastName, customerMobile, customerPhone, orderStatus, paymentMethod, shippingCosts, notes, deliveryAddress, now, null, null, null, null);
	}

	// TODO: Security...
	public Order findOrderById(Long orderId) {
		return orderDAO.findById(orderId);
	}

  // TODO: Security...
	public Order updateOrderAsPaid(Order order) {
		Date now = new Date();
		return orderDAO.updateOrderStatus(orderDAO.updatePaid(order, now), OrderStatus.PAID);
	}

  // TODO: Security...
	public Order updateOrderAsWaitingForDelivery(Order order) {
		Date now = new Date();
		return orderDAO.updateOrderStatus(orderDAO.updatePaid(order, now), OrderStatus.WAITING_FOR_DELIVERY);
	}
	
  // TODO: Security...
	public Order updateOrderAsCanceled(Order order) {
		Date now = new Date();
		return orderDAO.updateOrderStatus(orderDAO.updateCanceled(order, now), OrderStatus.CANCELED);
	}
	
	/* OrderItem */
	
	public OrderItem createOrderItem(Order order, Publication publication, String name, Double unitPrice, Integer count) {
		return orderItemDAO.create(order, publication, name, unitPrice, count);
	}

	public List<OrderItem> listOrderItems(Order order) {
		return orderItemDAO.listByOrder(order);
	}
	
}

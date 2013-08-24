package fi.foyt.fni.gamelibrary;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.gamelibrary.OrderDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.PaymentMethod;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateful
public class OrderController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private OrderDAO orderDAO;
	
	/* Order */
	
	public Order createOrder(User customer, OrderStatus orderStatus, PaymentMethod paymentMethod, Double shippingCosts, String notes, Address deliveryAddress) {
		Date now = new Date();
		return orderDAO.create(customer, orderStatus, paymentMethod, shippingCosts, notes, deliveryAddress, now, null, null, null);
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

	public Order updateOrderAsCanceled(Order order) {
		Date now = new Date();
		return orderDAO.updateOrderStatus(orderDAO.updateCanceled(order, now), OrderStatus.CANCELED);
	}
	
}

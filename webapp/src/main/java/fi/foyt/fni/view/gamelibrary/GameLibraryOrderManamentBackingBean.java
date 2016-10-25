package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/ordermanagement/", to = "/gamelibrary/ordermanagement.jsf")
@LoggedIn
@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
public class GameLibraryOrderManamentBackingBean {

  @Inject
  private OrderController orderController;

  @Inject
  private NavigationController navigationController;
  
  @RequestAction
  public String init() {
    ordersNew = orderController.listOrdersByStatus(OrderStatus.NEW);
    ordersCanceled = orderController.listOrdersByStatus(OrderStatus.CANCELED);
    ordersPaid = orderController.listOrdersByStatus(OrderStatus.PAID);
    ordersWaitingForDelivery = orderController.listOrdersByStatus(OrderStatus.WAITING_FOR_DELIVERY);
    ordersShipped = orderController.listOrdersByStatus(OrderStatus.SHIPPED);
    ordersDelivered = orderController.listOrdersByStatus(OrderStatus.DELIVERED);
    
    return null;
  }

  public List<Order> getOrdersNew() {
    return ordersNew;
  }
  
  public List<Order> getOrdersCanceled() {
    return ordersCanceled;
  }

  public List<Order> getOrdersPaid() {
    return ordersPaid;
  }

  public List<Order> getOrdersWaitingForDelivery() {
    return ordersWaitingForDelivery;
  }
  
  public List<Order> getOrdersShipped() {
    return ordersShipped;
  }

  public List<Order> getOrdersDelivered() {
    return ordersDelivered;
  }
  
  public Double getOrderTotalPrice(Long orderId) {
    Double result = 0d;
    
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      List<OrderItem> orderItems = orderController.listOrderItems(order);
      for (OrderItem orderItem : orderItems) {
        result += orderItem.getUnitPrice() * orderItem.getCount(); 
      }
      
      if (order.getShippingCosts() != null) {
        result += order.getShippingCosts();
      }
    }
    
    return result;
  }

  public String moveToCanceled(Long orderId) {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      orderController.updateOrderAsCanceled(order);
    } else {
      return navigationController.notFound();
    }
    
    return "/gamelibrary/ordermanagement.jsf?faces-redirect=true";
  }

  public String moveToPaid(Long orderId) {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      if (order.getDeliveryAddress() == null) {
        orderController.updateOrderAsPaid(order);
      } else {
        orderController.updateOrderAsWaitingForDelivery(order);
      }
    } else {
      return navigationController.notFound();
    }
    
    return "/gamelibrary/ordermanagement.jsf?faces-redirect=true";
  }

  public String moveToShipped(Long orderId) {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      orderController.updateOrderAsShipped(order);
    } else {
      return navigationController.notFound();
    }

    return "/gamelibrary/ordermanagement.jsf?faces-redirect=true";
  }

  public String moveToDelivered(Long orderId) {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      orderController.updateOrderAsDelivered(order);
    } else {
      return navigationController.notFound();
    }

    return "/gamelibrary/ordermanagement.jsf?faces-redirect=true";
  }
  
  private List<Order> ordersNew;
  private List<Order> ordersCanceled;
  private List<Order> ordersPaid;
  private List<Order> ordersWaitingForDelivery;
  private List<Order> ordersShipped;
  private List<Order> ordersDelivered;
}

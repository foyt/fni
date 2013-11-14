package fi.foyt.fni.view.gamelibrary;

import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
    @URLMapping(
        id = "gamelibrary-ordermanagement", 
        pattern = "/gamelibrary/ordermanagement/", 
        viewId = "/gamelibrary/ordermanagement.jsf"
    ) 
})
public class GameLibraryOrderManamentBackingBean {

  @Inject
  private OrderController orderController;
  
  @URLAction
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public void load() {
    ordersNew = orderController.listOrdersByStatus(OrderStatus.NEW);
    ordersCanceled = orderController.listOrdersByStatus(OrderStatus.CANCELED);
    ordersPaid = orderController.listOrdersByStatus(OrderStatus.PAID);
    ordersWaitingForDelivery = orderController.listOrdersByStatus(OrderStatus.WAITING_FOR_DELIVERY);
    ordersShipped = orderController.listOrdersByStatus(OrderStatus.SHIPPED);
    ordersDelivered = orderController.listOrdersByStatus(OrderStatus.DELIVERED);
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Order> getOrdersNew() {
    return ordersNew;
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Order> getOrdersCanceled() {
    return ordersCanceled;
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Order> getOrdersPaid() {
    return ordersPaid;
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Order> getOrdersWaitingForDelivery() {
    return ordersWaitingForDelivery;
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Order> getOrdersShipped() {
    return ordersShipped;
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Order> getOrdersDelivered() {
    return ordersDelivered;
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
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

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public String moveToCanceled(Long orderId) throws FileNotFoundException {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      orderController.updateOrderAsCanceled(order);
    } else {
      throw new FileNotFoundException();
    }
    
    return "pretty:gamelibrary-ordermanagement";
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public String moveToPaid(Long orderId) throws FileNotFoundException {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      if (order.getDeliveryAddress() == null) {
        orderController.updateOrderAsPaid(order);
      } else {
        orderController.updateOrderAsWaitingForDelivery(order);
      }
    } else {
      throw new FileNotFoundException();
    }
    
    return "pretty:gamelibrary-ordermanagement";
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public String moveToShipped(Long orderId) throws FileNotFoundException {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      orderController.updateOrderAsShipped(order);
    } else {
      throw new FileNotFoundException();
    }

    return "pretty:gamelibrary-ordermanagement";
  }

  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public String moveToDelivered(Long orderId) throws FileNotFoundException {
    Order order = orderController.findOrderById(orderId);
    if (order != null) {
      orderController.updateOrderAsDelivered(order);
    } else {
      throw new FileNotFoundException();
    }

    return "pretty:gamelibrary-ordermanagement";
  }
  
  private List<Order> ordersNew;
  private List<Order> ordersCanceled;
  private List<Order> ordersPaid;
  private List<Order> ordersWaitingForDelivery;
  private List<Order> ordersShipped;
  private List<Order> ordersDelivered;
}

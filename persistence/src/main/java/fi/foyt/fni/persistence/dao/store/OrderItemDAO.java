package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.Order;
import fi.foyt.fni.persistence.model.store.OrderItem;
import fi.foyt.fni.persistence.model.store.Product;

@RequestScoped
@DAO
public class OrderItemDAO extends GenericDAO<OrderItem> {
  
	public OrderItem create(Order order, Product product, String name, Double unitPrice, Integer count) {
		OrderItem orderItem = new OrderItem();
		
		orderItem.setCount(count);
		orderItem.setName(name);
		orderItem.setOrder(order);
		orderItem.setProduct(product);
		orderItem.setUnitPrice(unitPrice);
		
		getEntityManager().persist(orderItem);
		
		return orderItem;
	}
	
}

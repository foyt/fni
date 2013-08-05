package fi.foyt.fni.persistence.dao.gamelibrary;


import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;

@DAO
public class OrderItemDAO extends GenericDAO<OrderItem> {
  
	private static final long serialVersionUID = 1L;

	public OrderItem create(Order order, Publication publication, String name, Double unitPrice, Integer count) {
		OrderItem orderItem = new OrderItem();
		
		orderItem.setCount(count);
		orderItem.setName(name);
		orderItem.setOrder(order);
		orderItem.setPublication(publication);
		orderItem.setUnitPrice(unitPrice);
		
		getEntityManager().persist(orderItem);
		
		return orderItem;
	}
	
}

package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.OrderItem_;

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

	public List<OrderItem> listByOrder(Order order) {
		EntityManager entityManager = getEntityManager();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<OrderItem> criteria = criteriaBuilder.createQuery(OrderItem.class);
		Root<OrderItem> root = criteria.from(OrderItem.class);
		criteria.select(root);
		criteria.where(criteriaBuilder.equal(root.get(OrderItem_.order), order));

		return entityManager.createQuery(criteria).getResultList();
	}

	public List<Order> listOrdersByPublication(Publication publication) {
		EntityManager entityManager = getEntityManager();

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Order> criteria = criteriaBuilder.createQuery(Order.class);
		Root<OrderItem> root = criteria.from(OrderItem.class);
		criteria.select(root.get(OrderItem_.order)).distinct(true);
		criteria.where(criteriaBuilder.equal(root.get(OrderItem_.publication), publication));

		return entityManager.createQuery(criteria).getResultList();
	}

}

package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCart;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCartItem;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCartItem_;

@DAO
public class ShoppingCartItemDAO extends GenericDAO<ShoppingCartItem> {
  
	private static final long serialVersionUID = 1L;

	public ShoppingCartItem create(ShoppingCart cart, Publication publication, Integer count) {
		ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
		shoppingCartItem.setCart(cart);
		shoppingCartItem.setCount(count);
		shoppingCartItem.setProduct(publication);
		getEntityManager().persist(shoppingCartItem);
		
		return shoppingCartItem;
	}

	public List<ShoppingCartItem> listByCart(ShoppingCart cart) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ShoppingCartItem> criteria = criteriaBuilder.createQuery(ShoppingCartItem.class);
    Root<ShoppingCartItem> root = criteria.from(ShoppingCartItem.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(ShoppingCartItem_.cart), cart)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public List<ShoppingCartItem> listByCartAndProduct(ShoppingCart cart, Publication publication) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ShoppingCartItem> criteria = criteriaBuilder.createQuery(ShoppingCartItem.class);
    Root<ShoppingCartItem> root = criteria.from(ShoppingCartItem.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(ShoppingCartItem_.cart), cart)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public ShoppingCartItem updateCount(ShoppingCartItem shoppingCartItem, Integer count) {
		shoppingCartItem.setCount(count);
		getEntityManager().persist(shoppingCartItem);
		return shoppingCartItem;
	}
	
}

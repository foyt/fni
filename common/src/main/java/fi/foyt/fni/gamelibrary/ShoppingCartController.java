package fi.foyt.fni.gamelibrary;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import fi.foyt.fni.persistence.dao.gamelibrary.OrderDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.OrderItemDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ShoppingCartDAO;
import fi.foyt.fni.persistence.dao.gamelibrary.ShoppingCartItemDAO;
import fi.foyt.fni.persistence.model.gamelibrary.OrderStatus;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCart;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCartItem;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.Login;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.session.UserSessionEvent;
import fi.foyt.fni.users.UserController;

@Stateful
@SessionScoped
public class ShoppingCartController {

	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;

	@Inject
	private ShoppingCartDAO shoppingCartDAO;

	@Inject
	private ShoppingCartItemDAO shoppingCartItemDAO;
	
	@Inject
	private OrderDAO orderDAO;
	
	@Inject
	private OrderItemDAO orderItemDAO;
	
	public ShoppingCart getShoppingCart() {
		if (shoppingCartId != null) {
			// We already have a shopping cart defined in this session
			return shoppingCartDAO.findById(shoppingCartId);
		} else {
			// We do not have a shopping cart for this session
			ShoppingCart shoppingCart = null;
			
			String sessionId = ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).getId();
			
			if (sessionController.isLoggedIn()) {
				// If user is logged in we try to find existing cart by user 
				User loggedUser = sessionController.getLoggedUser();
				shoppingCart = shoppingCartDAO.findByCustomer(loggedUser);
				if (shoppingCart == null) {
					// If one does not exist, we need to create it
					Date now = new Date();
					shoppingCart = shoppingCartDAO.create(loggedUser, null, null, null, null, now, now);
				}
			} else {
				// If user is not yet logged in we have no option but to create new cart
				Date now = new Date();
				shoppingCart = shoppingCartDAO.create(null, sessionId, null, null, null, now, now);
			}
			
			shoppingCartId = shoppingCart.getId();
			return shoppingCart;
		}
	}

	public List<ShoppingCartItem> getShoppingCartItems() {
		return shoppingCartItemDAO.listByCart(getShoppingCart());
	}

	public void addProduct(Publication publication) {
		List<ShoppingCartItem> items = getShoppingCartItems();
		for (ShoppingCartItem item : items) {
			if (item.getPublication().getId().equals(publication.getId())) {
				shoppingCartItemDAO.updateCount(item, item.getCount() + 1);
				return;
			} 
		}
		
		shoppingCartItemDAO.create(getShoppingCart(), publication, 1);
	}
	
	public void setProductCount(Publication publication, Integer count) {
		List<ShoppingCartItem> items = getShoppingCartItems();
		for (ShoppingCartItem item : items) {
			if (item.getPublication().getId().equals(publication.getId())) {
				setItemCount(item, count);
				return;
			}
		}
		
		shoppingCartItemDAO.create(getShoppingCart(), publication, count);
	}

	public void setItemCount(ShoppingCartItem item, Integer count) {
		if (count == 0) {
			shoppingCartItemDAO.delete(item);
		} else {
			shoppingCartItemDAO.updateCount(item, count);
		}
	}
	
	public void removeProduct(Publication publication) {
		setProductCount(publication, 0);
	}
	
	public boolean isShoppingCartEmpty() {
		if (shoppingCartId == null) {
			return true;
		}
		
		return getShoppingCartItems().size() == 0;
	}
	
	private void cancelOrder(ShoppingCart shoppingCart) {
		Date now = new Date();
		orderDAO.create(shoppingCart.getCustomer(), OrderStatus.CANCELED, shoppingCart.getPaymentMethod(), null, null, shoppingCart.getDeliveryAddress(), now, now, null, null, null);
		shoppingCartDAO.delete(shoppingCart);
	}
	
	public void loginObserver(@Observes @Login UserSessionEvent event) {
		if (shoppingCartId != null) {
			// Session already has a bound cart 

			User loggedUser = userController.findUserById(event.getUserId());
			ShoppingCart existingShoppingCart = shoppingCartDAO.findByCustomer(loggedUser);
			
			if (existingShoppingCart != null) {
				// ... and we have a existing cart in the database
				ShoppingCart sessionCart = shoppingCartDAO.findById(shoppingCartId);
				if (isShoppingCartEmpty()) {
					// ... and our current cart is empty, so we can use the one in the database 
					// and just remove the empty cart
					shoppingCartDAO.delete(sessionCart);
					shoppingCartId = existingShoppingCart.getId();
				} else {
					// ... and our current cart has items within, so we need to cancel the old order
					// and bind this cart into logged user
					cancelOrder(existingShoppingCart);
					shoppingCartDAO.updateCustomer(sessionCart, loggedUser);
				}
			}
		}
		
	}

	private Long shoppingCartId = null;
}

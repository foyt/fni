package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCartItem;

@Stateful
@RequestScoped
@Named
public class MiniShoppingCartBackingBean {
	
	@Inject
	private SessionShoppingCartController sessionShoppingCartController;

	public boolean isShoppingCartEmpty() {
		return sessionShoppingCartController.isShoppingCartEmpty();
	}
	
	public List<ShoppingCartItem> getShoppingCartItems() { 
		return sessionShoppingCartController.getShoppingCartItems();
	}
	
	public Double getItemCosts() {
		Double result = 0d;
		for (ShoppingCartItem item : sessionShoppingCartController.getShoppingCartItems()) {
			result += item.getCount() * item.getPublication().getPrice();
		}

		return result;
	}
	
}

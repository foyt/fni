package fi.foyt.fni.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.gamelibrary.ShoppingCartItemDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.ShoppingCart;

@Dependent
@Stateful
public class ShoppingCartController {

	@Inject
	private ShoppingCartItemDAO shoppingCartItemDAO;
	
	public List<ShoppingCart> listShoppingCartsByPublication(Publication publication) {
		return shoppingCartItemDAO.listShoppingCartsByPublication(publication);
	}

}

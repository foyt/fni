package fi.foyt.fni.view.store;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.ProductController;

@SessionScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "store-product-most-recent", 
		pattern = "/gamelibrary/", 
		viewId = "/gamelibrary/index.jsf"
  )
})
public class ProductMostRecentListBackingBean extends AbstractProductListBackingBean {

	private static final int MAX_RECENT_PRODUCTS = 10;

	@Inject
	private ProductController productController;

	@Inject
	private ShoppingCartController shoppingCartController;

	@Inject
	private ForumController forumController;
	
	@URLAction
	public void init() {
		setProducts(productController.listRecentProducts(MAX_RECENT_PRODUCTS));
	}
	
}
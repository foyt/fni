package fi.foyt.fni.view.store;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.store.StoreController;

@RequestScoped
@Named
@Stateful
public class ProductListViewBackingBean {

	@Inject
	private StoreController storeController;
	
	@Inject
	private ShoppingCartController shoppingCartController;
	
	public void addProductToCart() {
		Product product = storeController.findProductById(productId);
		if (product == null) {
			// TODO: Proper error handling
			throw new RuntimeException("Invalid productId");
		}
		
		if (!product.getPublished()) {
			// TODO: Proper error handling
			throw new RuntimeException("Cannot add unpublished product into a shopping cart");
		}
		
		shoppingCartController.addProduct(product);
	}
	
	public Long getProductId() {
		return productId;
	}
	
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	private Long productId;
}
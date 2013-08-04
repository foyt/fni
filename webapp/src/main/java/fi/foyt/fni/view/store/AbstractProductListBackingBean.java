package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.store.ProductImage;
import fi.foyt.fni.persistence.model.store.ProductTag;
import fi.foyt.fni.persistence.model.store.StoreTag;

public class AbstractProductListBackingBean {
	
	@Inject
	private ProductController productController;
	
	@Inject
	private StoreTagController storeTagController;
	
	@Inject
	private ForumController forumController;

	@Inject
	private ShoppingCartController shoppingCartController;

	protected void setProducts(List<Product> products) {
		this.products = products;
	}
	
	public List<Product> getProducts() {
		return products;
	}
	
	public BookProduct getBookProduct(Product product) {
		if (product instanceof BookProduct) {
			return (BookProduct) product;
		}
		
		return null;
	}
	
	public Long getProductCommentCount(Product product) {
		if (product.getForumTopic() != null) {
			return forumController.countPostsByTopic(product.getForumTopic());
		}
		
		return null;
	}
	
	public List<StoreTag> getTags(Product product) {
		List<StoreTag> result = new ArrayList<>();

		List<ProductTag> productTags = storeTagController.listProductTags(product);
		for (ProductTag productTag : productTags) {
			result.add(productTag.getTag());
		}

		return result;
	}
	
	public List<ProductImage> getProductImages(Product product) {
		return productController.listProductImageByProduct(product);
	}
	
	public ProductImage getFirstImage(Product product) {
		return productController.listProductImageByProduct(product).get(0);
	}

	public boolean hasImages(Product product) {
		return productController.listProductImageByProduct(product).size() > 0;
	}

	public boolean hasSeveralImages(Product product) {
		return productController.listProductImageByProduct(product).size() > 1;
	}
	
	public void addProductToShoppingCart(Product product) {
		shoppingCartController.addProduct(product);
	}
	
	private List<Product> products;
}
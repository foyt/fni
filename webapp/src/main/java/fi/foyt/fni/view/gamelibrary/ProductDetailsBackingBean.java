package fi.foyt.fni.view.gamelibrary;

import java.io.FileNotFoundException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.gamelibrary.StoreTagController;
import fi.foyt.fni.persistence.model.gamelibrary.BookProduct;
import fi.foyt.fni.persistence.model.gamelibrary.Product;
import fi.foyt.fni.persistence.model.gamelibrary.ProductImage;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

@SessionScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "store-product", 
		pattern = "/gamelibrary/#{productDetailsBackingBean.urlName}", 
		viewId = "/gamelibrary/productdetails.jsf"
  )
})
public class ProductDetailsBackingBean {
	
	@Inject
	private ProductController productController;
	
	@Inject
	private StoreTagController storeTagController;

	@Inject
	private ForumController forumController;

	@Inject
	private ShoppingCartController shoppingCartController;
	
	@URLAction
	public void init() throws FileNotFoundException {
		this.product = productController.findProductByUrlName(getUrlName());
		if (this.product == null) {
			throw new FileNotFoundException();
		}
		
		tags = storeTagController.listProductGameLibraryTags(product);
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public BookProduct getBookProduct() {
		return bookProduct;
	}
	
	public List<GameLibraryTag> getTags() {
		return tags;
	}

	public boolean getHasSeveralImages() {
		return productController.listProductImageByProduct(product).size() > 1;
	}

	public boolean getHasImages() {
		return productController.listProductImageByProduct(product).size() > 0;
	}

	public ProductImage getFirstImage() {
		return productController.listProductImageByProduct(product).get(0);
	}
	
	public List<ProductImage> getProductImages() {
		return productController.listProductImageByProduct(product);
	}

	public Long getProductCommentCount() {
		if (product.getForumTopic() != null) {
			return forumController.countPostsByTopic(product.getForumTopic());
		}
		
		return null;
	}
	
	public void addProductToShoppingCart() {
		shoppingCartController.addProduct(product);
	}
	
	private String urlName;
	private Product product;
	private BookProduct bookProduct;
	private List<GameLibraryTag> tags;
}
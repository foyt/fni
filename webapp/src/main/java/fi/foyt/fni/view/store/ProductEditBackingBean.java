package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.persistence.model.gamelibrary.BookProduct;
import fi.foyt.fni.persistence.model.gamelibrary.Product;
import fi.foyt.fni.persistence.model.gamelibrary.ProductTag;
import fi.foyt.fni.persistence.model.gamelibrary.StoreTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Stateful
@RequestScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
  		id = "store-product-dialog-edit", 
  		pattern = "/gamelibrary/publications/#{productEditBackingBean.productId}/dialog/edit", 
  		viewId = "/gamelibrary/dialogs/editproduct.jsf"
  )
})
public class ProductEditBackingBean extends AbstractProductEditBackingBean {
	
	@Inject
	private ProductController productController;

	@Inject
	private StoreTagController storeTagController;
	
	@Inject
	private SessionController sessionController;
	
	@PostConstruct
	public void init() {
		setTagSelectItems(
  	  createTagSelectItems(storeTagController.listStoreTags())		
		);
	}
	
	@URLAction (onPostback = false)
	public void load() {
		Product product = productController.findProductById(getProductId());
		setProductName(product.getName());
		setProductDescription(product.getDescription());
		setProductPrice(product.getPrice());
		setProductRequiresDelivery(product.getRequiresDelivery());
		setProductPurchasable(product.getPurchasable());
		setProductDownloadable(false);
		setProductWeight(product.getWeight());
		setProductWidth(product.getWidth());
		setProductHeight(product.getHeight());
		setProductDepth(product.getDepth());
		
		if (product instanceof BookProduct) {
			setProductDownloadable(((BookProduct) product).getDownloadable());
			setBookAuthor(((BookProduct) product).getAuthor());
			setBookNumberOfPages(((BookProduct) product).getNumberOfPages());
		}
		
		List<String> tagList = new ArrayList<>();
		List<ProductTag> productTags = storeTagController.listProductTags(product);
		for (ProductTag productTag : productTags) {
			tagList.add(productTag.getTag().getText());
		}
		
		setProductTags(StringUtils.join(tagList, ';'));
	}
	
	public void save() {
		Product product = productController.findProductById(getProductId());
		if (product instanceof BookProduct) {
			BookProduct bookProduct = (BookProduct) product;
			User loggedUser = sessionController.getLoggedUser();
			
			List<StoreTag> tags = new ArrayList<>();
			String tagsString = getProductTags();
			
			if (StringUtils.isNotBlank(tagsString)) {
	  		for (String tag : tagsString.split(";")) {
	  			StoreTag storeTag = storeTagController.findTagByText(tag);
	  			if (storeTag == null) {
	  				storeTag = storeTagController.createTag(tag);
	  			}
	  			tags.add(storeTag);
	  		}
			}
			
			productController.updateBookProduct(bookProduct, 
				getProductPrice(), 
				getProductName(), 
				getProductDescription(), 
				tags, 
				product.getPublished(), 
				getProductRequiresDelivery(), 
				getProductDownloadable(), 
				getProductPurchasable(),
				getProductWeight(),
				getProductWidth(),
				getProductHeight(),
				getProductDepth(),
				getBookNumberOfPages(),
				getBookAuthor(),
				loggedUser);
		} else {
			// TODO: Proper error handling
			throw new RuntimeException("Could not persist unknown product");
		}
	}
	
}

package fi.foyt.fni.view.store;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.persistence.model.store.BookProduct;
import fi.foyt.fni.persistence.model.store.StoreTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Stateful
@RequestScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
  		id = "store-product-dialog-create", 
  		pattern = "/store/product/dialog/create", 
  		viewId = "/store/dialogs/createproduct.jsf"
  )
})
public class ProductCreateBackingBean extends AbstractProductEditBackingBean {
	
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

	public void save() {
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
		
		BookProduct bookProduct = productController.createBookProduct(loggedUser, 
			getProductName(), 
			getProductDescription(), 
			getProductRequiresDelivery(), 
			getProductDownloadable(), 
			getProductPurchasable(),
			getProductPrice(),
			null,
			getProductHeight(), 
			getProductWidth(),
			getProductDepth(),
			getProductWeight(),
			getBookAuthor(),
			getBookNumberOfPages(),
			tags
		);
		
		setProductId(bookProduct.getId());
	}
	
}

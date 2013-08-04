package fi.foyt.fni.view.gamelibrary;

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

import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.BookProduct;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Stateful
@RequestScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
  		id = "store-product-dialog-create", 
  		pattern = "/gamelibrary/publications/dialog/create", 
  		viewId = "/gamelibrary/dialogs/createproduct.jsf"
  )
})
public class ProductCreateBackingBean extends AbstractProductEditBackingBean {
	
	@Inject
	private ProductController productController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private SessionController sessionController;
	
	@PostConstruct
	public void init() {
		setTagSelectItems(
			  createTagSelectItems(gameLibraryTagController.listGameLibraryTags())		
			);
	}

	public void save() {
		User loggedUser = sessionController.getLoggedUser();
		List<GameLibraryTag> tags = new ArrayList<>();
		String tagsString = getProductTags();
		
		if (StringUtils.isNotBlank(tagsString)) {
  		for (String tag : tagsString.split(";")) {
  			GameLibraryTag gameLibraryTag = gameLibraryTagController.findTagByText(tag);
  			if (gameLibraryTag == null) {
  				gameLibraryTag = gameLibraryTagController.createTag(tag);
  			}
  			tags.add(gameLibraryTag);
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

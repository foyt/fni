package fi.foyt.fni.view.gamelibrary;

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

import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Stateful
@RequestScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
  		id = "gamelibrary-product-dialog-edit", 
  		pattern = "/gamelibrary/publications/#{productEditBackingBean.productId}/dialog/edit", 
  		viewId = "/gamelibrary/dialogs/editproduct.jsf"
  )
})
public class ProductEditBackingBean extends AbstractProductEditBackingBean {
	
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
	
	@URLAction (onPostback = false)
	public void load() {
		Publication publication = productController.findProductById(getProductId());
		setProductName(publication.getName());
		setProductDescription(publication.getDescription());
		setProductPrice(publication.getPrice());
		setProductRequiresDelivery(publication.getRequiresDelivery());
		setProductPurchasable(publication.getPurchasable());
		setProductDownloadable(false);
		setProductWeight(publication.getWeight());
		setProductWidth(publication.getWidth());
		setProductHeight(publication.getHeight());
		setProductDepth(publication.getDepth());
		
		if (publication instanceof BookPublication) {
			setProductDownloadable(((BookPublication) publication).getDownloadable());
			setBookAuthor(((BookPublication) publication).getAuthor());
			setBookNumberOfPages(((BookPublication) publication).getNumberOfPages());
		}
		
		List<String> tagList = new ArrayList<>();
		List<PublicationTag> publicationTags = gameLibraryTagController.listProductTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			tagList.add(publicationTag.getTag().getText());
		}
		
		setProductTags(StringUtils.join(tagList, ';'));
	}
	
	public void save() {
		Publication publication = productController.findProductById(getProductId());
		if (publication instanceof BookPublication) {
			BookPublication bookPublication = (BookPublication) publication;
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
			
			productController.updateBookProduct(bookPublication, 
				getProductPrice(), 
				getProductName(), 
				getProductDescription(), 
				tags, 
				publication.getPublished(), 
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

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
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;

@SessionScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-publication", 
		pattern = "/gamelibrary/#{publicationDetailsBackingBean.urlName}", 
		viewId = "/gamelibrary/publicationdetails.jsf"
  )
})
public class PublicationDetailsBackingBean {
	
	@Inject
	private ProductController productController;
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ForumController forumController;

	@Inject
	private ShoppingCartController shoppingCartController;
	
	@URLAction
	public void init() throws FileNotFoundException {
		this.publication = productController.findProductByUrlName(getUrlName());
		if (this.publication == null) {
			throw new FileNotFoundException();
		}
		
		tags = gameLibraryTagController.listProductGameLibraryTags(publication);
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	public Publication getProduct() {
		return publication;
	}
	
	public BookPublication getBookProduct() {
		return bookPublication;
	}
	
	public List<GameLibraryTag> getTags() {
		return tags;
	}

	public boolean getHasSeveralImages() {
		return productController.listPublicationImagesByPublication(publication).size() > 1;
	}

	public boolean getHasImages() {
		return productController.listPublicationImagesByPublication(publication).size() > 0;
	}

	public PublicationImage getFirstImage() {
		return productController.listPublicationImagesByPublication(publication).get(0);
	}
	
	public List<PublicationImage> getProductImages() {
		return productController.listPublicationImagesByPublication(publication);
	}

	public Long getProductCommentCount() {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
	}
	
	public void addProductToShoppingCart() {
		shoppingCartController.addProduct(publication);
	}
	
	private String urlName;
	private Publication publication;
	private BookPublication bookPublication;
	private List<GameLibraryTag> tags;
}
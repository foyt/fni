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
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;

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
	private PublicationController publicationController;
	
	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ForumController forumController;

	@Inject
	private ShoppingCartController shoppingCartController;
	
	@URLAction
	public void init() throws FileNotFoundException {
		this.publication = publicationController.findPublicationByUrlName(getUrlName());
		if (this.publication == null) {
			throw new FileNotFoundException();
		}
		
		tags = gameLibraryTagController.listPublicationGameLibraryTags(publication);
		creativeCommonsLicense = CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	public Publication getPublication() {
		return publication;
	}
	
	public BookPublication getBookPublication() {
		return bookPublication;
	}
	
	public List<GameLibraryTag> getTags() {
		return tags;
	}

	public boolean getHasSeveralImages() {
		return publicationController.listPublicationImagesByPublication(publication).size() > 1;
	}

	public boolean getHasImages() {
		return publicationController.listPublicationImagesByPublication(publication).size() > 0;
	}

	public PublicationImage getFirstImage() {
		return publicationController.listPublicationImagesByPublication(publication).get(0);
	}
	
	public List<PublicationImage> getPublicationImages() {
		return publicationController.listPublicationImagesByPublication(publication);
	}

	public Long getPublicationCommentCount() {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
	}
	
	public void addPublicationToShoppingCart() {
		shoppingCartController.addPublication(publication);
	}
	
	public CreativeCommonsLicense getCreativeCommonsLicense() {
		return creativeCommonsLicense;
	}
	
	private String urlName;
	private Publication publication;
	private BookPublication bookPublication;
	private List<GameLibraryTag> tags;
	private CreativeCommonsLicense creativeCommonsLicense;
}
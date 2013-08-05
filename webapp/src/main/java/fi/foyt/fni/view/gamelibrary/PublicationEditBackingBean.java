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
  		id = "gamelibrary-publication-dialog-edit", 
  		pattern = "/gamelibrary/publications/#{publicationEditBackingBean.publicationId}/dialog/edit", 
  		viewId = "/gamelibrary/dialogs/editpublication.jsf"
  )
})
public class PublicationEditBackingBean extends AbstractPublicationEditBackingBean {
	
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
		Publication publication = productController.findProductById(getPublicationId());
		setPublicationName(publication.getName());
		setPublicationDescription(publication.getDescription());
		setPublicationPrice(publication.getPrice());
		setPublicationRequiresDelivery(publication.getRequiresDelivery());
		setPublicationPurchasable(publication.getPurchasable());
		setPublicationDownloadable(false);
		setPublicationWeight(publication.getWeight());
		setPublicationWidth(publication.getWidth());
		setPublicationHeight(publication.getHeight());
		setPublicationDepth(publication.getDepth());
		
		if (publication instanceof BookPublication) {
			setPublicationDownloadable(((BookPublication) publication).getDownloadable());
			setBookAuthor(((BookPublication) publication).getAuthor());
			setBookNumberOfPages(((BookPublication) publication).getNumberOfPages());
		}
		
		List<String> tagList = new ArrayList<>();
		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			tagList.add(publicationTag.getTag().getText());
		}
		
		setPublicationTags(StringUtils.join(tagList, ';'));
	}
	
	public void save() {
		Publication publication = productController.findProductById(getPublicationId());
		if (publication instanceof BookPublication) {
			BookPublication bookPublication = (BookPublication) publication;
			User loggedUser = sessionController.getLoggedUser();
			
			List<GameLibraryTag> tags = new ArrayList<>();
			String tagsString = getPublicationTags();
			
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
				getPublicationPrice(), 
				getPublicationName(), 
				getPublicationDescription(), 
				tags, 
				publication.getPublished(), 
				getPublicationRequiresDelivery(), 
				getPublicationDownloadable(), 
				getPublicationPurchasable(),
				getPublicationWeight(),
				getPublicationWidth(),
				getPublicationHeight(),
				getPublicationDepth(),
				getBookNumberOfPages(),
				getBookAuthor(),
				loggedUser);
		} else {
			// TODO: Proper error handling
			throw new RuntimeException("Could not persist unknown product");
		}
	}
	
}

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

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Stateful
@RequestScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
  		id = "gamelibrary-publication-dialog-create", 
  		pattern = "/gamelibrary/publications/dialog/create", 
  		viewId = "/gamelibrary/dialogs/createpublication.jsf"
  )
})
public class PublicationCreateBackingBean extends AbstractPublicationEditBackingBean {
	
	@Inject
	private PublicationController publicationController;

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
		
		BookPublication bookPublication = publicationController.createBookPublication(loggedUser, 
			getPublicationName(), 
			getPublicationDescription(), 
			getPublicationRequiresDelivery(), 
			getPublicationDownloadable(), 
			getPublicationPurchasable(),
			getPublicationPrice(),
			null,
			getPublicationHeight(), 
			getPublicationWidth(),
			getPublicationDepth(),
			getPublicationWeight(),
			getBookAuthor(),
			getBookNumberOfPages(),
			tags
		);
		
		setPublicationId(bookPublication.getId());
	}
	
}

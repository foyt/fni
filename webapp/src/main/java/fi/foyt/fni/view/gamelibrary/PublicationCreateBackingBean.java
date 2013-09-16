package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@Stateful
@RequestScoped
@Named
@URLMappings(mappings = {
  @URLMapping(
  		id = "gamelibrary-publication-create", 
  		pattern = "/gamelibrary/manage/createpublication", 
  		viewId = "/gamelibrary/createpublication.jsf"
  )
})
public class PublicationCreateBackingBean extends AbstractPublicationEditBackingBean {
	
	@Inject
	private PublicationController publicationController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private UserController userController;
	
	@Inject
	private SessionController sessionController;
	
	@URLAction
	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void init() {
		setTagSelectItems(createTagSelectItems(gameLibraryTagController.listGameLibraryTags()));
		setLicenseSelectItems(createLicenseSelectItems());
		setAuthorSelectItems(createAuthorSelectItems());
		setLicenseType("CC");	
	  setCreativeCommonsDerivatives("SHARE_ALIKE");
		setCreativeCommonsCommercial("YES");
	}

	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void save() throws IOException {
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
			getBookNumberOfPages(),
			getLicenseUrl(),
			tags
		);
		
		String[] authorIdsStr = StringUtils.split(getAuthorIds(), ",");
		
		for (String authorIdStr : authorIdsStr) {
			Long authorId = NumberUtils.createLong(authorIdStr);
			if (authorId == null) {
				// TODO: Proper error handling
				throw new RuntimeException("Invalid author id");
			} else {
				User author = userController.findUserById(authorId);
				if (author == null) {
					// TODO: Proper error handling
					throw new RuntimeException("Invalid author id");
				} else {
				  publicationController.createPublicationAuthor(bookPublication, author);
				}
			}
		}

		setPublicationId(bookPublication.getId());
	}
	
}

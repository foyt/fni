package fi.foyt.fni.view.gamelibrary.old;

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

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

//@Stateful
//@RequestScoped
//@Named
//@URLMappings(mappings = {
//  @URLMapping(
//  		id = "gamelibrary-publication-edit", 
//  		pattern = "/gamelibrary/manage/editpublication/#{publicationEditBackingBean.publicationId}", 
//  		viewId = "/gamelibrary/editpublication.jsf"
//  )
//})
public class PublicationEditBackingBean extends AbstractPublicationEditBackingBean {
	
	@Inject
	private PublicationController publicationController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private UserController userController;
	
	@Inject
	private SessionController sessionController;
	
//	@URLAction
	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void load() {
		setTagSelectItems(createTagSelectItems(gameLibraryTagController.listGameLibraryTags()));
		setLicenseSelectItems(createLicenseSelectItems());
		setAuthorSelectItems(createAuthorSelectItems());

		Publication publication = publicationController.findPublicationById(getPublicationId());
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
			setBookNumberOfPages(((BookPublication) publication).getNumberOfPages());
		}
		
		List<String> tagList = new ArrayList<>();
		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			tagList.add(publicationTag.getTag().getText());
		}
		
		setPublicationTags(StringUtils.join(tagList, ';'));
		CreativeCommonsLicense creativeCommonsLicense = CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
		
		if (creativeCommonsLicense != null) {
			setLicenseType("CC");	
			
			if (creativeCommonsLicense.getDerivatives()) {
				if (creativeCommonsLicense.getShareAlike()) {
				  setCreativeCommonsDerivatives("SHARE_ALIKE");
				} else {
				  setCreativeCommonsDerivatives("YES");
				}
			} else {
			  setCreativeCommonsDerivatives("NO");
			}
			
			if (creativeCommonsLicense.getCommercial()) {
				setCreativeCommonsCommercial("YES");
			} else {
				setCreativeCommonsCommercial("NO");
			}
			
		} else {
			setLicenseType("OTHER");	
			setCreativeCommonsDerivatives("YES");
			setCreativeCommonsCommercial("YES");
			setLicenseOther(publication.getLicense());
		}
		
		List<User> authors = new ArrayList<User>();
		List<Long> authorIds = new ArrayList<Long>();
		
		List<PublicationAuthor> publicationAuthors = publicationController.listPublicationAuthors(publication);
		for (PublicationAuthor publicationAuthor : publicationAuthors) {
			authors.add(publicationAuthor.getAuthor());
			authorIds.add(publicationAuthor.getAuthor().getId());
		}
		
		setAuthors(authors);
		setAuthorIds(StringUtils.join(authorIds, ','));
	}
	
	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void save() throws IOException {
		Publication publication = publicationController.findPublicationById(getPublicationId());
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
			
			publicationController.updateBookPublication(bookPublication, 
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
				loggedUser);
			
			List<PublicationAuthor> publicationAuthors = publicationController.listPublicationAuthors(bookPublication);
			List<Long> oldAuthorIds = new ArrayList<Long>();
			for (PublicationAuthor publicationAuthor : publicationAuthors) {
				oldAuthorIds.add(publicationAuthor.getAuthor().getId());
			}
			
			String[] authorIdsStr = StringUtils.split(getAuthorIds(), ",");
			
			for (String authorIdStr : authorIdsStr) {
				Long authorId = NumberUtils.createLong(authorIdStr);
				if (authorId == null) {
					// TODO: Proper error handling
					throw new RuntimeException("Invalid author id");
				} else {
  				if (!oldAuthorIds.contains(authorId)) {
  					User author = userController.findUserById(authorId);
  					if (author == null) {
  						// TODO: Proper error handling
  						throw new RuntimeException("Invalid author id");
  					} else {
  					  publicationController.createPublicationAuthor(bookPublication, author);
  					}
  				}
				}
				
				oldAuthorIds.remove(authorId);
			}
			
			for (Long removeAuthorId : oldAuthorIds) {
				User author = userController.findUserById(removeAuthorId);
				if (author != null) {
					PublicationAuthor publicationAuthor = publicationController.findPublicationAuthorByPublicationAndAuthor(bookPublication, author);
					if (publicationAuthor != null) {
						publicationController.deletePublicationAuthor(publicationAuthor);
					}
				}
			}
			
			publicationController.updateLicense(publication, getLicenseUrl());
		} else {
			// TODO: Proper error handling
			throw new RuntimeException("Could not persist unknown publication");
		}
	}
	
}

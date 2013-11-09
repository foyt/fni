package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@Named
@Stateful
public class GameLibraryPublicationsBackingBean {

	@Inject
	private PublicationController publicationController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ForumController forumController;

	@Inject
	private SessionShoppingCartController sessionShoppingCartController;

	public boolean hasImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication).size() > 0;
	}

	public List<GameLibraryTag> getTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<>();

		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}

		return result;
	}
	
	public List<User> getAuthors(Publication publication) {
		List<User> result = new ArrayList<>(); 

		List<PublicationAuthor> publicationAuthors = publicationController.listPublicationAuthors(publication);
		for (PublicationAuthor publicationAuthor : publicationAuthors) {
			result.add(publicationAuthor.getAuthor());
		}
		
		return result;
	}
	
	public Long getPublicationCommentCount(Publication publication) {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
	}
	
	public Integer getPublicationNumberOfPages(Publication publication) {
	  if (publication instanceof BookPublication) {
	    return ((BookPublication) publication).getNumberOfPages();
	  }
	  
	  return null;
	}
  
  public boolean isPublicationDownloadable(Publication publication) {
    if (publication instanceof BookPublication) {
      return ((BookPublication) publication).getDownloadableFile() != null;
    }
    
    return false;
  }

  public boolean isPublicationPurchasable(Publication publication) {
    if (publication instanceof BookPublication) {
      return ((BookPublication) publication).getPrintableFile() != null;
    }
    
    return false;
  }
  
	public void addPublicationToShoppingCart(Publication publication) {
		sessionShoppingCartController.addPublication(publication);
	}
	
	public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}
	
	public String replaceDescriptionLineBreaks(String description) {
	  if (StringUtils.isNotBlank(description)) {
	    return description.replace("\n", "<br/>");  
	  }
	  
	  return null;
	}
}

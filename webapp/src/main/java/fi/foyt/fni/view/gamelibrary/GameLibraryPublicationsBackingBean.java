package fi.foyt.fni.view.gamelibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

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
	
	@PostConstruct
	public void init() {
	  publicationAuthors = new HashMap<Long, List<User>>();
	  hasPublicationImages = new HashMap<Long, Boolean>();
	}

	public synchronized boolean hasImages(Publication publication) {
	  if (!hasPublicationImages.containsKey(publication.getId())) {
	    hasPublicationImages.put(publication.getId(), publicationController.listPublicationImagesByPublication(publication).size() > 0);
	  }
	  
		return hasPublicationImages.get(publication.getId());
	}

	public List<GameLibraryTag> getTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<>();

		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}

		return result;
	}
	
	public boolean hasAuthors(Publication publication) {
	  return getPublicationAuthors(publication).size() > 0;
	}
	
	public boolean hasSingleAuthor(Publication publication) {
    return getPublicationAuthors(publication).size() == 1;
	}
	
	public List<User> getAuthors(Publication publication) {
		return getPublicationAuthors(publication);
	}
	
	private synchronized List<User> getPublicationAuthors(Publication publication) {
	  if (!publicationAuthors.containsKey(publication.getId())) {
	    List<PublicationAuthor> authors = publicationController.listPublicationAuthors(publication);
      List<User> users = new ArrayList<User>(authors.size());
	    for (PublicationAuthor publicationAuthor : authors) {
	      users.add(publicationAuthor.getAuthor());
	    }
	    
	    publicationAuthors.put(publication.getId(), users);
	    
	    return users;
	  }
	  
    return publicationAuthors.get(publication.getId());
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
	
	private Map<Long, List<User>> publicationAuthors;
	private Map<Long, Boolean> hasPublicationImages;
}

package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.faces.FacesUtils;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/manage/", to = "/gamelibrary/manage.jsf")
@LoggedIn
@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
public class GameLibraryManageBackingBean {
	
	private static final String DEFAULT_LICENSE = "https://creativecommons.org/licenses/by-sa/3.0/";
	
	@Inject
	private OrderController orderController;

	@Inject
	private ShoppingCartController shoppingCartController; 
	
	@Inject
	private SessionController sessionController;

  @Inject
	private PublicationController publicationController;

  @Inject
	private ForumController forumController;

  @Inject
  private UserController userController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @PostConstruct
  public void init() {
  }
  
  public List<Publication> getUnpublishedPublications() {
  	return publicationController.listUnpublishedPublications();
  }
	
  public List<Publication> getPublishedPublications() {
  	return publicationController.listPublishedPublications();
  }
  
  public BookPublication getBookPublication(Publication publication) {
  	if (publication instanceof BookPublication) {
  		return (BookPublication) publication;
  	}
  	
  	return null;
  }
  
  public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}
  
  public void createBookPublication() throws IOException {
    Language defaultLanguage = systemSettingsController.getDefaultLanguage();
    
  	BookPublication bookPublication = publicationController.createBookPublication(
  	    sessionController.getLoggedUser(), 
  	    FacesUtils.getLocalizedValue("gamelibrary.manage.untitledBookName"), 
  	    null,
  	    0d,
  	    null,
  	    null,
  	    null,
  	    null,
  	    null,
  	    null,
  	    DEFAULT_LICENSE,
  	    null,
  	    defaultLanguage);
  	
  	FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/manage/")
  	  .append(bookPublication.getId())
  	  .append("/edit")
  	  .toString());
  }
  
  public String publish(Long publicationId) {
  	Publication publication = publicationController.findPublicationById(publicationId);
  	
  	if (publication.getForumTopic() == null) {
  	  Long forumId = systemSettingsController.getLongSetting(SystemSettingKey.GAMELIBRARY_PUBLICATION_FORUM_ID);
  	  String systemUserEmail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_USER_EMAIL);
  	  User systemUser = userController.findUserByEmail(systemUserEmail);
  	  Forum gameLibraryForum = forumController.findForumById(forumId);
  	  
  	  if ((gameLibraryForum != null) && (systemUser != null)) {
        String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
  	    String subject = publication.getName();
  	    String link = contextPath + "/gamelibrary/" + publication.getUrlName();
  	    
        Locale publicationLocale = publication.getLanguage().getLocale();
        String initialMessage = ExternalLocales.getText(publicationLocale, "gamelibrary.forum.initialMessage", link, subject);
  	    
        ForumTopic topic = forumController.createTopic(gameLibraryForum, subject, systemUser);
  	    forumController.createForumPost(topic, systemUser, initialMessage);
  	    publicationController.updatePublicationForumTopic(publication, topic);
  	  }
  	}
  	
  	publicationController.publishPublication(publication);
    return "/gamelibrary/manage.jsf?faces-redirect=true";
	}
  
  public String unpublish(Long publicationId) {
  	Publication publication = publicationController.findPublicationById(publicationId);
  	publicationController.unpublishPublication(publication);
  	return "/gamelibrary/manage.jsf?faces-redirect=true";
	}
	
	public boolean isRemovable(Long publicationId) {
		Publication publication = publicationController.findPublicationById(publicationId);

		if (orderController.listOrdersByPublication(publication).size() > 0) {
			return false;
		}
		
		if (shoppingCartController.listShoppingCartsByPublication(publication).size() > 0) {
			return false;
		}
		
		return true;
	}
  
  public String remove(Long publicationId) {
  	Publication publication = publicationController.findPublicationById(publicationId);
  	publicationController.deletePublication(publication);
    return "/gamelibrary/manage.jsf?faces-redirect=true";
	}

}

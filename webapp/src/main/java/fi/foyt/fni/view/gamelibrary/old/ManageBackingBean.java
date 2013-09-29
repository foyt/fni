package fi.foyt.fni.view.gamelibrary.old;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
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

//@RequestScoped
//@Named
//@Stateful
//@URLMappings(mappings = {
//  @URLMapping(
//		id = "gamelibrary-manage", 
//		pattern = "/gamelibrary/manage/", 
//		viewId = "/gamelibrary/manage.jsf"
//  )
//})
public class ManageBackingBean extends AbstractPublicationListBackingBean {

	@Inject
	private PublicationController publicationController;

	@Inject
	private ShoppingCartController shoppingCartController;

	@Inject
	private OrderController orderController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	@Inject
	private SessionController sessionController;

	@Inject
	private ForumController forumController;
	
	@Inject
	private UserController userController;
	
	@URLAction
	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void init() {
		User loggedUser = sessionController.getLoggedUser();
//		setPublications(publicationController.listUnpublishedPublications(loggedUser));
	}

	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void publish(Long publicationId) throws IOException {
		Publication publication = publicationController.findPublicationById(publicationId);
		if (publication.getForumTopic() == null) {
			String systemUserEmail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_USER_EMAIL);
			User systemUser = userController.findUserByEmail(systemUserEmail);
			Long forumId = systemSettingsController.getLongSetting(SystemSettingKey.GAMELIBRARY_PUBLICATION_FORUM_ID);
			Forum forum = forumController.findForumById(forumId);
			ForumTopic forumTopic = forumController.createTopic(forum, publication.getName(), systemUser);

			String publicationUrl = new StringBuilder()
	  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
	  	  .append("/gamelibrary/")
	  	  .append(publication.getUrlName())
	  	  .toString();
			
			String initialForumPostContent = FacesUtils.getLocalizedValue("gamelibrary.bookPublication.initialForumMessage", publicationUrl, publication.getName());
			forumController.createForumPost(forumTopic, systemUser, initialForumPostContent);
			
			publicationController.updatePublicationForumTopic(publication, forumTopic);
		}

		publicationController.publishPublication(publication);

		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/")
  	  .toString());
	}
	
	public boolean getDeletable(Long publicationId) {
		Publication publication = publicationController.findPublicationById(publicationId);

		if (orderController.listOrdersByPublication(publication).size() > 0) {
			return false;
		}
		
		if (shoppingCartController.listShoppingCartsByPublication(publication).size() > 0) {
			return false;
		}
		
		return true;
	}

	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void delete(Long publicationId) throws IOException {
		publicationController.deletePublication(publicationController.findPublicationById(publicationId));

		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/manage/")
  	  .toString());
	}
	
}
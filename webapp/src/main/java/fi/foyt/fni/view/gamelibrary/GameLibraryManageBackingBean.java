package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.ShoppingCartController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;


@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-manage", 
		pattern = "/gamelibrary/manage/", 
		viewId = "/gamelibrary/manage.jsf"
  )
})
public class GameLibraryManageBackingBean {
	
	private static final String DEFAULT_LICENSE = "http://creativecommons.org/licenses/by-sa/3.0/";
	
	@Inject
	private OrderController orderController;

	@Inject
	private ShoppingCartController shoppingCartController; 
	
	@Inject
	private SessionController sessionController;
	
	@Inject
	private PublicationController publicationController;

  @URLAction
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public void init() {
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Publication> getUnpublishedPublications() {
  	return publicationController.listUnpublishedPublications();
  }
	
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public List<Publication> getPublishedPublications() {
  	return publicationController.listPublishedPublications();
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public BookPublication getBookPublication(Publication publication) {
  	if (publication instanceof BookPublication) {
  		return (BookPublication) publication;
  	}
  	
  	return null;
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public void createBookPublication() throws IOException {
  	BookPublication bookPublication = publicationController.createBookPublication(sessionController.getLoggedUser(), FacesUtils.getLocalizedValue("gamelibrary.manage.untitledBookName"), DEFAULT_LICENSE);
  	
  	FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/manage/")
  	  .append(bookPublication.getId())
  	  .append("/edit")
  	  .toString());
  }
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public void publish(Long publicationId) {
  	Publication publication = publicationController.findPublicationById(publicationId);
  	publicationController.publishPublication(publication);
	}
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public void unpublish(Long publicationId) {
  	Publication publication = publicationController.findPublicationById(publicationId);
  	publicationController.unpublishPublication(publication);
	}
	
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
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
  
  @LoggedIn
  @Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
  public void remove(Long publicationId) {
  	Publication publication = publicationController.findPublicationById(publicationId);
  	publicationController.deletePublication(publication);
	}

}

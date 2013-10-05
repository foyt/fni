package fi.foyt.fni.view.gamelibrary;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

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
  
	public CreativeCommonsLicense getCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}

}

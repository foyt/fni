package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;

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
public class ManageBackingBean extends AbstractPublicationListBackingBean {

	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;
	
	@URLAction
	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void init() {
		User loggedUser = sessionController.getLoggedUser();
		setPublications(publicationController.listUnpublishedPublications(loggedUser));
	}

	@LoggedIn
	@Secure (Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)
	public void publish(Long publicationId) throws IOException {
		publicationController.publishPublication(publicationController.findPublicationById(publicationId));
		
		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  	  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  	  .append("/gamelibrary/")
  	  .toString());
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
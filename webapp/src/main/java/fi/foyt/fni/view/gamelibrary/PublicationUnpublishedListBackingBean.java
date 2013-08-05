package fi.foyt.fni.view.gamelibrary;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-publication-unpublished", 
		pattern = "/gamelibrary/unpublished/", 
		viewId = "/gamelibrary/unpublishedlist.jsf"
  )
})
public class PublicationUnpublishedListBackingBean extends AbstractPublicationListBackingBean {

	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;
	
	@URLAction
	@LoggedIn
	public void init() {
		User loggedUser = sessionController.getLoggedUser();
		setPublications(publicationController.listUnpublishedProducts(loggedUser));
	}
	
}
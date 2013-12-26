package fi.foyt.fni.view.gamelibrary;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "gamelibrary-publication", 
		pattern = "/gamelibrary/#{gameLibraryPublicationBackingBean.urlName}", 
		viewId = "/gamelibrary/publication.jsf"
  )
})
public class GameLibraryPublicationBackingBean {
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private PublicationController publicationController;
  
  @URLAction
  public void init() throws FileNotFoundException {
    publication = publicationController.findPublicationByUrlName(getUrlName());
    if (publication == null) {
      throw new FileNotFoundException();
    }
    
    if (!publication.getPublished()) {
      if (!sessionController.isLoggedIn()) {
        throw new UnauthorizedException();
      }
      
      if (!sessionController.hasLoggedUserRole(Role.GAME_LIBRARY_MANAGER)) {
        throw new ForbiddenException();
      }
    }
  }
  
	public Publication getPublication() {
		return publication;
	}
	
	public String getUrlName() {
		return urlName;
	}
	
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}
	
	private String urlName;
	private Publication publication;
}
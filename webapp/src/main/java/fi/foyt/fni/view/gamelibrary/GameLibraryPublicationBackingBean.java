package fi.foyt.fni.view.gamelibrary;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/{urlName}", to = "/gamelibrary/publication.jsf")
public class GameLibraryPublicationBackingBean {
  
  @Parameter
  private String urlName;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private PublicationController publicationController;
  
  @RequestAction
  public String init() throws FileNotFoundException {
    publication = publicationController.findPublicationByUrlName(getUrlName());
    if (publication == null) {
      return "/error/not-found.jsf";
    }
    
    if (!publication.getPublished()) {
      if (!sessionController.isLoggedIn()) {
        throw new UnauthorizedException();
      }
      
      if (!sessionController.hasLoggedUserRole(Role.GAME_LIBRARY_MANAGER)) {
        throw new ForbiddenException();
      }
    }
    
    return null;
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
	
	private Publication publication;
}
package fi.foyt.fni.view.gamelibrary;

import java.io.FileNotFoundException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/{urlName}", to = "/gamelibrary/publication.jsf")
public class GameLibraryPublicationBackingBean {
  
  @Parameter
  @Matches ("[a-zA-Z0-9_]{1,}")
  private String urlName;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private PublicationController publicationController;

  @Inject
  private NavigationController navigationController;
  
  @RequestAction
  public String init() throws FileNotFoundException {
    publication = publicationController.findPublicationByUrlName(getUrlName());
    if (publication == null) {
      return navigationController.notFound();
    }
    
    if (!publication.getPublished()) {
      if (!sessionController.isLoggedIn()) {
        throw new UnauthorizedException();
      }

      if (!publication.getCreator().getId().equals(sessionController.getLoggedUserId())) {
        if (!sessionController.hasLoggedUserRole(Role.GAME_LIBRARY_MANAGER)) {
          return navigationController.accessDenied();
        }
      }
    }
    
    if (StringUtils.isNotBlank(publication.getDescriptionPlain())) {
      metaDescription = StringEscapeUtils.escapeHtml4(publication.getDescriptionPlain());
    } else {
      metaDescription = "";
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
	
	public String getMetaDescription() {
    return metaDescription;
  }
	
	private Publication publication;
	private String metaDescription;
}
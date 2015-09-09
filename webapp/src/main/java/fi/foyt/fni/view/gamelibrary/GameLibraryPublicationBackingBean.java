package fi.foyt.fni.view.gamelibrary;

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
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Named
@Stateful
@Join (path = "/gamelibrary/{urlName}", to = "/gamelibrary/publication.jsf")
public class GameLibraryPublicationBackingBean {
  
  @Parameter
  @Matches ("[a-zA-Z0-9_.-]{1,}")
  private String urlName;
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private PublicationController publicationController;

  @Inject
  private NavigationController navigationController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @RequestAction
  public String init() {
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
    
    metaTitle = publication.getName();
    metaUrl = String.format("%s/gamelibrary/%s", systemSettingsController.getSiteUrl(true, true), publication.getUrlName());
    metaImage = String.format("%s/gamelibrary/publicationImages/%d", systemSettingsController.getSiteUrl(true, true), publication.getDefaultImage().getId());
    metaLocale = publication.getLanguage() != null ? publication.getLanguage().getLocale().toString() : "";
    
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
	
	public String getMetaImage() {
    return metaImage;
  }
	
	public String getMetaLocale() {
    return metaLocale;
  }
	
	public String getMetaTitle() {
    return metaTitle;
  }
	
	public String getMetaUrl() {
    return metaUrl;
  }
	
	private Publication publication;
	private String metaDescription;
	private String metaTitle;
	private String metaUrl;
	private String metaImage;
	private String metaLocale;
}
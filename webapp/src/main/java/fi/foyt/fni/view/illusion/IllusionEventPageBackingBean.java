package fi.foyt.fni.view.illusion;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/pages/{materialPath}", to = "/illusion/event-page.jsf")
public class IllusionEventPageBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Parameter
  @Matches("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String materialPath;

  @Inject
  private MaterialController materialController;

  @Inject
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private SessionController sessionController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setEventUrlName(getUrlName());

    Material material = materialController.findMaterialByPath(illusionEvent.getFolder(), getMaterialPath());
    if ((material == null) || (material.getType() != MaterialType.ILLUSION_GROUP_DOCUMENT)) {
      return "/error/not-found.jsf";
    }
    
    IllusionEventDocument document = (IllusionEventDocument) material;
    if (document.getDocumentType() != IllusionEventDocumentType.PAGE) {
      return "/error/not-found.jsf";
    }
    
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      IllusionEventPageVisibility visibility = illusionEventPageController.getPageVisibility(illusionEvent, document.getId().toString());
      if (visibility != IllusionEventPageVisibility.VISIBLE) {
        if (visibility == IllusionEventPageVisibility.HIDDEN) {
          return "/error/access-denied.jsf";
        }
        
        if (visibility == IllusionEventPageVisibility.PARTICIPANTS) {
          if (!sessionController.isLoggedIn()) {
            String redirectUrl = String.format("%s/illusion/event/%s/pages/%s", systemSettingsController.getSiteContextPath(), getUrlName(), getMaterialPath());
            try {
              return "/users/login.jsf?faces-redirect=true&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
              return "/error/internal-error.jsf";
            }
          }
          
          if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.PARTICIPANT)) {
            return "/error/access-denied.jsf";
          }
        }
      }
    }

    pageTitle = material.getTitle();
    pageContent = document.getData();
    illusionEventNavigationController.setSelectedPage(material.getId().toString());

    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }

  public String getMaterialPath() {
    return materialPath;
  }

  public void setMaterialPath(String materialPath) {
    this.materialPath = materialPath;
  }
  
  public String getPageTitle() {
    return pageTitle;
  }
  
  public String getPageContent() {
    return pageContent;
  }
  
  private String pageTitle;
  private String pageContent;
}

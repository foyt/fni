package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/pages/{materialPath}", to = "/illusion/event-page.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_ACCESS)
@SecurityContext(context = "@urlName")
public class IllusionEventPageBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Parameter
  @Matches("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String materialPath;

  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if (participant == null) {
      return "/error/access-denied.jsf";
    }

    illusionEventNavigationController.setEventUrlName(getUrlName());

    Material material = materialController.findMaterialByPath(illusionEvent.getFolder(), getMaterialPath());
    if ((material == null) || (material.getType() != MaterialType.ILLUSION_GROUP_DOCUMENT)) {
      return "/error/not-found.jsf";
    }
    
    IllusionEventDocument document = (IllusionEventDocument) material;
    if (document.getDocumentType() != IllusionEventDocumentType.PAGE) {
      return "/error/not-found.jsf";
    }

    if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER) {
      User loggedUser = sessionController.getLoggedUser();
      if (!materialPermissionController.isPublic(loggedUser, material) && !materialPermissionController.hasAccessPermission(loggedUser, material)) {
        return "/error/access-denied.jsf";
      }
    }

    pageTitle = material.getTitle();
    pageContent = document.getData();
    illusionEventNavigationController.setSelectedPage(material.getId());

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

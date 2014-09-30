package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.illusion.IllusionEventNavigationController.SelectedPage;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/materials/{materialPath}", to = "/illusion/event-material.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_ACCESS, deferred = true)
@SecurityContext (context = "@urlName")
public class IllusionEventMaterialBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;
  
  @Parameter
  @Matches ("[a-zA-Z0-9_/.\\-:,]{1,}")
  private String materialPath;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private MaterialPermissionController materialPermissionController;

  @Inject
  private SessionController sessionController;
  
  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;
  
  @Inject
  private HttpServletRequest httpServletRequest;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if (participant == null) {
      return "/error/access-denied.jsf";
    }
    
    illusionEventNavigationController.setSelectedPage(SelectedPage.MATERIALS);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    Material material = materialController.findMaterialByPath(illusionEvent.getFolder(), getMaterialPath());
    if (material == null) {
      return "/error/not-found.jsf";
    }
    
    User loggedUser = sessionController.getLoggedUser();
    
    if (!materialPermissionController.isPublic(loggedUser, material) && !materialPermissionController.hasAccessPermission(loggedUser, material)) {
      return "/error/access-denied.jsf";
    }
    
    String contextPath =  httpServletRequest.getContextPath();
    
    materialUrl = contextPath + "/materials/" + material.getPath();
    if (material.getType() == MaterialType.CHARACTER_SHEET) {
      String dataUrl = contextPath + "/rest/illusion/events/" + illusionEvent.getId() + "/materials/" + material.getId() + "/participantSettings/" + participant.getId();
      materialUrl += "?dataUrl=" + dataUrl;
    }
    
    switch (material.getType()) {
      case IMAGE:
        materialEmbedType = EmbedType.IMG;
      break;
      case VECTOR_IMAGE:
        materialEmbedType = EmbedType.SVG;
      break;
      default:
        materialEmbedType = EmbedType.IFRAME;
      break;
    }
    
    materialTitle = material.getTitle();
    
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
  
  public String getMaterialUrl() {
    return materialUrl;
  }
  
  public EmbedType getMaterialEmbedType() {
    return materialEmbedType;
  }
  
  public String getMaterialTitle() {
    return materialTitle;
  }
  
  private String materialUrl;
  private EmbedType materialEmbedType;
  private String materialTitle;
  
  public enum EmbedType {
    IFRAME,
    IMG,
    SVG
  }
}

package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventMaterialController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
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
@Join (path = "/illusion/event/{urlName}/materials/{materialPath}", to = "/illusion/event-material.jsf")
@LoggedIn
@Secure (value = Permission.ILLUSION_EVENT_ACCESS)
@SecurityContext (context = "@urlName")
public class IllusionEventMaterialBackingBean extends AbstractIllusionEventBackingBean {

  @Inject
  private Logger logger;
  
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
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private IllusionEventMaterialController illusionEventMaterialController;

  @Inject
  private JadeController jadeController;
  
  @Inject
  private HttpServletRequest httpServletRequest;

  @Inject
  private NavigationController navigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    if (participant == null) {
      return navigationController.accessDenied();
    }
    
    if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER) {
      if (!illusionEvent.getPublished()) {
        return navigationController.accessDenied();
      }
      
      IllusionEventPageVisibility visibility = illusionEventPageController.getPageVisibility(illusionEvent, IllusionEventPage.Static.MATERIALS.name());
      if (visibility == IllusionEventPageVisibility.HIDDEN) {
        return navigationController.accessDenied();
      }
    }

    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MATERIALS);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    Material material = materialController.findMaterialByPath(illusionEvent.getFolder(), getMaterialPath());
    if (material == null) {
      return navigationController.notFound();
    }
    
    if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER) {
      User loggedUser = sessionController.getLoggedUser();
      if (!materialPermissionController.isPublic(loggedUser, material) && !materialPermissionController.hasAccessPermission(loggedUser, material)) {
        return navigationController.accessDenied();
      }
    }

    String contextPath =  httpServletRequest.getContextPath();
    String materialUrl = contextPath + "/materials/" + material.getPath();
    if (material.getType() == MaterialType.CHARACTER_SHEET) {
      String key = null;
      
      synchronized (this) {
        IllusionEventMaterialParticipantSetting webSocketKeySetting = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(material, participant, IllusionEventMaterialParticipantSettingKey.WEBSOCKET_KEY);
        if (webSocketKeySetting == null) {
          webSocketKeySetting = illusionEventMaterialController.createParticipantSetting(material, participant, IllusionEventMaterialParticipantSettingKey.WEBSOCKET_KEY, UUID.randomUUID().toString());
        }
        
        key = webSocketKeySetting.getValue();
      }
      
      materialUrl += "?contextPath=" + contextPath + "&participantId=" + participant.getId() + "&eventId=" + illusionEvent.getId() + "&materialId=" + material.getId() + "&key=" + key;
    }
    
    EmbedType materialEmbedType = null;
    
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
    
    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, IllusionEventPage.Static.MATERIALS)
        .addBreadcrumb(illusionEvent, "/materials", ExternalLocales.getText(sessionController.getLocale(), "illusion.breadcrumbs.materials"))
        .addBreadcrumb(illusionEvent, "/materials/" + getMaterialPath(), material.getTitle())
        .put("materialTitle", material.getTitle())
        .put("materialUrl", materialUrl)
        .put("materialEmbedType", materialEmbedType.toString());
      
    try {
      Map<String, Object> templateModel = templateModelBuilder.build(sessionController.getLocale());
      headHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/material-head", templateModel);
      contentsHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/material-contents", templateModel);
    } catch (JadeException | IOException e) {
      logger.log(Level.SEVERE, "Could not parse jade template", e);
      return navigationController.internalError();
    }
  
    
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
  
  public String getHeadHtml() {
    return headHtml;
  }
  
  public String getContentsHtml() {
    return contentsHtml;
  }

  private String headHtml;

  private String contentsHtml;
  
  private enum EmbedType {
    IFRAME,
    IMG,
    SVG
  }
}

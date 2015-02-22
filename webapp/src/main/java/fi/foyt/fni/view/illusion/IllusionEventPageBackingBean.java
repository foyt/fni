package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jsf.NavigationController;
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

  @Inject
  private Logger logger;
  
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
  private JadeController jadeController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private NavigationController navigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setEventUrlName(getUrlName());

    Material material = materialController.findMaterialByPath(illusionEvent.getFolder(), getMaterialPath());
    if (material == null) {
      material = materialController.findMaterialByPermaLink(illusionEvent.getFolder().getPath() + "/" + getMaterialPath());
      if (material != null) {
        return "/illusion/event-page.jsf?faces-redirect=true&urlName=" + getUrlName() + "&materialPath=" + material.getUrlName();
      }
    }
    
    if ((material == null) || (material.getType() != MaterialType.ILLUSION_GROUP_DOCUMENT)) {
      return navigationController.notFound();
    }
    
    IllusionEventDocument document = (IllusionEventDocument) material;
    if (document.getDocumentType() != IllusionEventDocumentType.PAGE) {
      return navigationController.notFound();
    }
    
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      if (!illusionEvent.getPublished()) {
        return navigationController.accessDenied();
      }

      IllusionEventPageVisibility visibility = illusionEventPageController.getPageVisibility(illusionEvent, document.getId().toString());
      if (visibility != IllusionEventPageVisibility.VISIBLE) {
        if (visibility == IllusionEventPageVisibility.HIDDEN) {
          return navigationController.accessDenied();
        }
        
        if (visibility == IllusionEventPageVisibility.PARTICIPANTS) {
          if (!sessionController.isLoggedIn()) {
            String redirectUrl = String.format("%s/illusion/event/%s/pages/%s", systemSettingsController.getSiteContextPath(), getUrlName(), getMaterialPath());
            try {
              return "/users/login.jsf?faces-redirect=true&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
              return navigationController.internalError();
            }
          }
          
          if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.PARTICIPANT)) {
            return navigationController.accessDenied();
          }
        }
      }
    }

    illusionEventNavigationController.setSelectedPage(material.getId().toString());
    pageTitle = material.getTitle();
    
    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, material.getId().toString())
        .put("pageContent", document.getData())
        .put("pageTitle", pageTitle)
        .addBreadcrumb(illusionEvent, "/pages/" + getMaterialPath(), pageTitle);
    
    try {
      Map<String, Object> templateModel = templateModelBuilder.build(sessionController.getLocale());
      headHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/page-head", templateModel);
      contentsHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/page-contents", templateModel);
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
  
  public String getPageTitle() {
    return pageTitle;
  }

  private String headHtml;
  private String contentsHtml;
  private String pageTitle;
}

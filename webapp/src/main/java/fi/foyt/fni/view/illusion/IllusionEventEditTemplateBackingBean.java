package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventTemplate;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/edit-template", to = "/illusion/event-edit-template.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext(context = "@urlName")
public class IllusionEventEditTemplateBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Parameter
  private Long templateId;
  
  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MANAGE_TEMPLATES);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    IllusionEventTemplate template = illusionEventController.findEventTemplateById(getTemplateId());
    if (template == null) {
      return "/error/not-found.jsf";
    }
    
    if (!template.getEvent().getId().equals(illusionEvent.getId())) {
      return "/error/not-found.jsf";
    }
    
    templateName = template.getName();
    templateData = template.getData();
    
    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public String getTemplateName() {
    return templateName;
  }
  
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }
  
  public String getTemplateData() {
    return templateData;
  }
  
  public void setTemplateData(String templateData) {
    this.templateData = templateData;
  }
  
  public Long getTemplateId() {
    return templateId;
  }
  
  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
  }
  
  public String save() {
    IllusionEventTemplate template = illusionEventController.findEventTemplateById(getTemplateId());
    illusionEventController.updateEventTemplateName(template, getTemplateName());
    illusionEventController.updateEventTemplateData(template, getTemplateData());
    return "/illusion/event-edit-template.jsf?faces-redirect=true&urlName=" + getUrlName() + "&templateId=" + getTemplateId();
  }
  
  private String templateName;
  private String templateData;
}

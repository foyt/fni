package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
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
@Join(path = "/illusion/event/{urlName}/manage-templates", to = "/illusion/event-manage-templates.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext(context = "@urlName")
public class IllusionEventManageTemplatesBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant member) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MANAGE_PAGES);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    templates = illusionEventController.listTemplates(illusionEvent);
    
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
  
  public List<IllusionEventTemplate> getTemplates() {
    return templates;
  }
  
  public String newTemplate() {
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
    IllusionEventTemplate template = illusionEventController.createEventTemplate(event);
    return "/illusion/event-edit-template.jsf?faces-redirect=true&urlName=" + event.getUrlName() + "&templateId=" + template.getId();
  }
  
  public String deleteTemplate() {
    if (StringUtils.isNotBlank(getTemplateName())) {
      IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
      IllusionEventTemplate template = illusionEventController.findEventTemplate(event, getTemplateName());
      if (template != null) {
        illusionEventController.deleteEventTemplate(template);
        return "/illusion/event-manage-templates.jsf?faces-redirect=true&urlName=" + event.getUrlName();
      }
    }
    
    return null;
  }

  private List<IllusionEventTemplate> templates;
  private String templateName;
}

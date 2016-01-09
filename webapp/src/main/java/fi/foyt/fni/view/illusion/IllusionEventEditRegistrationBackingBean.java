package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/edit-registration", to = "/illusion/event-edit-registration.jsf")
@LoggedIn
@Secure(value = Permission.ILLUSION_EVENT_MANAGE)
@SecurityContext(context = "@urlName")
public class IllusionEventEditRegistrationBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private NavigationController navigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.MANAGE_TEMPLATES);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(illusionEvent);
    if (form != null) {
      formData = form.getData();
    } else {
      formData = "";
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
  
  public String getFormData() {
    return formData;
  }
  
  public void setFormData(String formData) {
    this.formData = formData;
  }
  
  public String save() {
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
    if (event == null) {
      return navigationController.notFound();
    }
    
    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(event);
    if (form != null) {
      illusionEventController.updateEventRegistrationForm(form, formData);
    } else {
      illusionEventController.createEventRegistrationForm(event, formData);
    }
    
    return "/illusion/event-edit-registration.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  private String formData;
}

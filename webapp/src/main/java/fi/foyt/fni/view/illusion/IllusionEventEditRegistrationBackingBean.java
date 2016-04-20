package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.registration.FormReader;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

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
  private Logger logger;
  
  @Inject
  private SessionController sessionController;
  
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
      formData = loadDefaultTemplate();
    }
    
    return null;
  }

  private String loadDefaultTemplate() {
    ClassLoader classLoader = getClass().getClassLoader();

    String path = String.format("fi/foyt/fni/illusion/registration/empty_form_%s.json", sessionController.getLocale().getLanguage());
    
    try {
      InputStream templateStream = classLoader.getResourceAsStream(path);
      try {
        return IOUtils.toString(templateStream);
      } finally {
        templateStream.close();
      }
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load initial registration form", e);
      return "{}";
    }
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
    
    FormReader formReader = new FormReader(formData);
    if (formReader.getForm() == null) {
      String message = null;
      
      if ((formReader.getParseError() instanceof JsonMappingException) && (formReader.getParseError().getCause() instanceof JsonParseException)) {
        message = ((JsonParseException) formReader.getParseError().getCause()).getOriginalMessage();
      } else if (formReader.getParseError() != null) {
        message = formReader.getParseError().getMessage();
      } else {
        message = "";
      }
      
      FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.editRegistration.couldNotParseWarning", message));
    } else {
      if (StringUtils.isBlank(formReader.getEmailField())) {
        FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.editRegistration.noEmailWarning"));
      } else {
        if (!formReader.isRequiredField(formReader.getEmailField())) {
          FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.editRegistration.emailNotRequiredWarning"));
        }
      }
    }

    return "/illusion/event-edit-registration.jsf?faces-redirect=true&urlName=" + getUrlName();
  }
  
  private String formData;
}

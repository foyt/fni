package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/event-registration", to = "/illusion/event-registration.jsf")
public class IllusionEventRegistrationBackingBean extends AbstractIllusionEventBackingBean {
  
  @Inject
  private Logger logger;
  
  @Parameter
  private String urlName;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private SessionController sessionController;

  @Inject
  private JadeController jadeController;

  @Inject
  private NavigationController navigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      if (!illusionEvent.getPublished()) {
        return navigationController.accessDenied();
      }
    }

    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(illusionEvent);
    if (form == null) {
      return navigationController.notFound();
    }

    String currentPageId = IllusionEventPage.Static.INDEX.name();
    illusionEventNavigationController.setSelectedPage(currentPageId);

    Map<String, String> answers = illusionEventController.loadRegistrationFormAnswers(form, participant);
    
    String formData = null;
    try {
      formData = new ObjectMapper().writeValueAsString(answers);
    } catch (JsonProcessingException e1) {
      logger.log(Level.SEVERE, String.format("Failed to read form answers for form %d, participant %d", form.getId(), participant.getId()), e1);
      return navigationController.internalError();
    }
        
    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, currentPageId)
        .put("formSchema", form.getData())
        .put("formData", formData)
        .addBreadcrumb(illusionEvent, "/event-registration", ExternalLocales.getText(sessionController.getLocale(), "illusion.registration.navigationRegistration"));
    
    try {
      Map<String, Object> templateModel = templateModelBuilder.build(sessionController.getLocale());
      headHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/registration-head", templateModel);
      contentsHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/registration-contents", templateModel);
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
  
  public String getHeadHtml() {
    return headHtml;
  }
  
  public String getContentsHtml() {
    return contentsHtml;
  }
  
  public String getAnswers() {
    return answers;
  }
  
  public void setAnswers(String answers) {
    this.answers = answers;
  }
  
  public String save() {
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(getUrlName());
    if (event == null) {
      return navigationController.notFound();
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if (participant == null) {
      return navigationController.accessDenied();
    }
    
    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(event);
    if (form == null) {
      logger.log(Level.SEVERE, String.format("Could not find registration form from event %d", event.getId()));
      return navigationController.internalError();
    }
    
    Map<String, String> answers = null;
    
    try {
      answers = (new ObjectMapper()).readValue(getAnswers(), new TypeReference<Map<String, String>>() {});
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to read registration form values", e);
      return navigationController.internalError();
    }
    
    
    illusionEventController.saveRegistrationFormAnswers(form, participant, answers);

    return String.format("/illusion/event-registration.jsf?urlName=%s", getUrlName());
  }
  
  private String headHtml;
  private String contentsHtml;
  private String answers;
  
}

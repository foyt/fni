package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.illusion.registration.FormReader;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jade.JadeLocaleHelper;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

// TODO: Mail organizers about new participants

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
  private UserController userController;

  @Inject
  private AuthenticationController authenticationController;

  @Inject
  private JadeController jadeController;

  @Inject
  private NavigationController navigationController;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private Mailer mailer;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      if (!illusionEvent.getPublished()) {
        return navigationController.accessDenied();
      }
    }

    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(illusionEvent);
    if ((form == null) || StringUtils.isBlank(form.getData())) {
      return navigationController.notFound();
    }

    FormReader formReader = new FormReader(form.getData());
    if (formReader.getForm() == null) {
      logger.severe(String.format("Failed to read event %d registration form", illusionEvent.getId()));
      return navigationController.notFound();
    }
    
    String currentPageId = IllusionEventPage.Static.INDEX.name();
    illusionEventNavigationController.setSelectedPage(currentPageId);
    
    String formData = null;
    List<String> readonlyFields = new ArrayList<>();

    if (participant != null) {
      if (!hasPermissionToJoin(participant)) {
        return navigationController.accessDenied();
      }
      
      User user = participant.getUser();
      
      Map<String, String> answers = illusionEventController.loadRegistrationFormAnswers(form, participant);
      
      String emailField = formReader.getEmailField();
      if (StringUtils.isNotBlank(emailField) && StringUtils.isBlank(answers.get(emailField))) {
        answers.put(emailField, userController.getUserPrimaryEmail(user));
      }

      String firstNameField = formReader.getFirstNameField();
      if (StringUtils.isNotBlank(firstNameField) && StringUtils.isBlank(answers.get(firstNameField))) {
        answers.put(firstNameField, user.getFirstName());
      }

      String lastNameField = formReader.getLastNameField();
      if (StringUtils.isNotBlank(lastNameField) && StringUtils.isBlank(answers.get(lastNameField))) {
        answers.put(lastNameField, user.getLastName());
      }
      
      try {
        formData = new ObjectMapper().writeValueAsString(answers);
      } catch (JsonProcessingException e1) {
        logger.log(Level.SEVERE, String.format("Failed to read form answers for form %d, participant %d", form.getId(), participant.getId()), e1);
        return navigationController.internalError();
      }
      
      if (StringUtils.isNotBlank(emailField)) {
        readonlyFields.add(emailField);
      }
    }
    
    String readonlyFieldsJSON;
    try {
      readonlyFieldsJSON = new ObjectMapper().writeValueAsString(readonlyFields);
    } catch (JsonProcessingException e1) {
      logger.log(Level.SEVERE, String.format("Failed to serialize readonly fields for event %d", illusionEvent.getId()), e1);
      return navigationController.internalError();
    }
        
    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, currentPageId)
        .put("formSchema", form.getData())
        .put("formData", formData)
        .put("readonlyFields", readonlyFieldsJSON)
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
    
    IllusionEventParticipant participant = null;
    
    if (sessionController.isLoggedIn()) {
      participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    }
    
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      if (!event.getPublished()) {
        return navigationController.accessDenied();
      }
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

    FormReader formReader = new FormReader(form.getData());
    boolean newUser = false;
    String password = null;
    
    if (participant == null) {
      if (sessionController.isLoggedIn()) {
        // User is logged in but is not yet a participant in this event, so we just create new one
        participant = createNewParticipant(sessionController.getLoggedUser(), event);
      } else {
        String emailField = formReader.getEmailField();
        if (StringUtils.isBlank(emailField)) {
          logger.log(Level.SEVERE, "Could not resolve email field");
          return navigationController.internalError();
        }
  
        String registrantEmail = answers.get(emailField);
        if (StringUtils.isBlank(registrantEmail)) {
          logger.log(Level.SEVERE, "Could not resolve email");
          return navigationController.internalError();
        }
        
        User user = userController.findUserByEmail(registrantEmail);
        if (user == null) {
          // it's a brand new user
          password = RandomStringUtils.randomAlphabetic(5);
          String firstName = formReader.getFirstNameField() != null ? answers.get(formReader.getFirstNameField()) : null;
          String lastName = formReader.getLastNameField() != null ? answers.get(formReader.getLastNameField()) : null;
          user = createNewUser(firstName, lastName, registrantEmail, password);
          participant = createNewParticipant(user, event);
          newUser = true;
        } else {
          // existing user, new participant
          participant = createNewParticipant(user, event);
        }
      }
    } else {
      if (!hasPermissionToJoin(participant)) {
        return navigationController.accessDenied();
      }
    }
    
    illusionEventController.saveRegistrationFormAnswers(form, participant, answers);
    
    sendConfirmRegistrationMail(event, participant, newUser, password);
    
    return String.format("/illusion/event-registration.jsf?urlName=%s", getUrlName());
  }
  
  private User createNewUser(String firstName, String lastName, String email, String password) {
    Date now = new Date();
    
    User user = userController.createUser(firstName, lastName, null, sessionController.getLocale(), now, UserProfileImageSource.GRAVATAR);
    userController.createUserEmail(user, email, Boolean.TRUE);
    String passwordEncoded = DigestUtils.md5Hex(password);
    InternalAuth internalAuth = authenticationController.createInternalAuth(user, passwordEncoded);
    // TODO: verification
    authenticationController.verifyInternalAuth(internalAuth);
    
    return user;
  }

  private boolean hasPermissionToJoin(IllusionEventParticipant participant) {
    switch (participant.getRole()) {
      case BANNED:
      case BOT:
        return false;
      default:
        return true;
    }
  }

  private IllusionEventParticipant createNewParticipant(User user, IllusionEvent event) {
    IllusionEventParticipantRole participantRole = event.getJoinMode() == IllusionEventJoinMode.APPROVE 
        ? IllusionEventParticipantRole.PENDING_APPROVAL
        : IllusionEventParticipantRole.PARTICIPANT;
    
    return illusionEventController.createIllusionEventParticipant(user, event, null, participantRole);
  }
  
  private void sendConfirmRegistrationMail(IllusionEvent illusionEvent, IllusionEventParticipant participant, boolean newUser, String password) {
    Map<String, Object> templateModel = new HashMap<>();
    
    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(illusionEvent);
    User user = participant.getUser();
    String email = userController.getUserPrimaryEmail(user);
    
    Map<String, String> answers = illusionEventController.loadRegistrationFormAnswers(form, participant);
    FormReader formReader = new FormReader(form.getData());
    List<Map<String, Object>> formDatas = new ArrayList<>();
    
    for (String field : formReader.getFields()) {
      Map<String, Object> data = new HashMap<>();
      data.put("label", formReader.getFieldLabel(field));
      data.put("value", StringUtils.replace(answers.get(field), "\n", "<br/>"));
      formDatas.add(data);
    }
    
    templateModel.put("eventName", illusionEvent.getName());
    templateModel.put("firstName", user.getFirstName());
    templateModel.put("lastName", user.getLastName());
    templateModel.put("email", email);
    templateModel.put("newUser", newUser);
    templateModel.put("role", participant.getRole());
    templateModel.put("password", password);
    templateModel.put("formDatas", formDatas);
    templateModel.put("locale", new JadeLocaleHelper(sessionController.getLocale()));
          
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    String toName = getParticipantDisplayName(participant);
    String subject = ExternalLocales.getText(sessionController.getLocale(), "illusion.registration.registeredMail.subject", illusionEvent.getName());
    String content = null;
    try {
      content = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/mail-confirm-registration", templateModel);
    } catch (JadeException | IOException e) {
      logger.log(Level.SEVERE, "Failed to render registration mail template", e);
      return;
    }
    
    try {
      mailer.sendMail(fromMail, fromName, email, toName, subject, content, "text/html");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Failed to send registration mail", e);
      return;
    }
  }
  
  private String headHtml;
  private String contentsHtml;
  private String answers;
  
}

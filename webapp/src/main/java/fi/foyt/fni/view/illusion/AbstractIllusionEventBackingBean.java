package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.RequestAction;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionJadeTemplateLoader;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.illusion.registration.FormReader;
import fi.foyt.fni.jade.JadeLocaleHelper;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

public abstract class AbstractIllusionEventBackingBean {

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private SessionController sessionController;

  @Inject
  private UserController userController;

  @Inject
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private IllusionJadeTemplateLoader templateLoader;

  @Inject
  private IllusionTemplateModelBuilderFactory illusionTemplateModelBuilderFactory;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private JadeController jadeController;
  
  @Inject
  private Mailer mailer;
  
  @Inject
  private NavigationController navigationController;

  @RequestAction
  public String basicInit() {
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(getUrlName());
    if (illusionEvent == null) {
      return navigationController.notFound();
    }
    
    IllusionEventParticipant participant = null;
    
    if (sessionController.isLoggedIn()) {
      User loggedUser = sessionController.getLoggedUser();
  
      participant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, loggedUser);
    }
    
    IllusionEventFolder folder = illusionEvent.getFolder();
    
    id = illusionEvent.getId();
    name = illusionEvent.getName();
    description = illusionEvent.getDescription();
    illusionFolderPath = folder.getPath();
    mayManageEvent = participant != null ? participant.getRole() == IllusionEventParticipantRole.ORGANIZER : false;
    
    jadeConfiguration = new JadeConfiguration();
    jadeConfiguration.setTemplateLoader(templateLoader);
    jadeConfiguration.setCaching(false);
  
    return init(illusionEvent, participant);
  }

  public abstract String init(IllusionEvent illusionEvent, IllusionEventParticipant participant);
  public abstract String getUrlName();
  
  public Long getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  
  public String getIllusionFolderPath() {
    return illusionFolderPath;
  }

  public boolean getMayManageEvent() {
    return mayManageEvent;
  }
  
  public String getParticipantDisplayName(IllusionEventParticipant participant) {
    return userController.getUserDisplayName(participant.getUser());
  }

  protected IllusionTemplateModelBuilder createDefaultTemplateModelBuilder(IllusionEvent illusionEvent, IllusionEventParticipant participant, fi.foyt.fni.illusion.IllusionEventPage.Static currentPage) {
    return createDefaultTemplateModelBuilder(illusionEvent, participant, currentPage.name());
  }
  
  protected IllusionTemplateModelBuilder createDefaultTemplateModelBuilder(IllusionEvent illusionEvent, IllusionEventParticipant participant, String currentPageId) {
    IllusionTemplateModelBuilder modelBuilder = illusionTemplateModelBuilderFactory
      .newBuilder();
    
    if (participant != null) {
      for (IllusionEventPage page : illusionEventPageController.listParticipantPages(illusionEvent)) {
        modelBuilder.addPage(page);
      }
    } else {
      for (IllusionEventPage page : illusionEventPageController.listPublicPages(illusionEvent)) {
        modelBuilder.addPage(page);
      }
    }
    
    if (sessionController.isLoggedIn()) {
      if (participant != null) {
        if (participant.getRole() == IllusionEventParticipantRole.ORGANIZER) {
          modelBuilder
            .addAdminPage(IllusionEventPage.Static.MANAGE_PAGES, "/manage-pages", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.pages"))
            .addAdminPage(IllusionEventPage.Static.PARTICIPANTS, "/participants", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.participants"))
            .addAdminPage(IllusionEventPage.Static.GROUPS, "/groups", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.groups"))
            .addAdminPage(IllusionEventPage.Static.SETTINGS, "/settings", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.settings"))
            .addAdminPage(IllusionEventPage.Static.MANAGE_TEMPLATES, "/manage-templates", ExternalLocales.getText(sessionController.getLocale(), "illusion.eventNavigation.manageTemplates")); 
        }
        
        modelBuilder.setUserInfo(sessionController.getLoggedUser(), participant);
      }
    }
    
    return modelBuilder
        .defaults(sessionController.getLocale())
        .setCurrentPage(currentPageId)
        .eventDefaults(illusionEvent);
  }

  protected JadeConfiguration getJadeConfiguration() {
    return jadeConfiguration;
  }
  
  private Map<String, Object> createRegistrationConfirmMailTemplateModel(IllusionEventParticipant participant, IllusionEvent illusionEvent, String email, boolean newUser, String password, Map<String, String> answers) {
    Map<String, Object> templateModel = new HashMap<>();
    
    User user = participant.getUser();
    List<Map<String, Object>> formDatas = createFormDatas(illusionEvent, email, answers, user);
    
    templateModel.put("eventName", illusionEvent.getName());
    templateModel.put("firstName", user.getFirstName());
    templateModel.put("lastName", user.getLastName());
    templateModel.put("email", email);
    templateModel.put("newUser", newUser);
    templateModel.put("role", participant.getRole());
    templateModel.put("password", password);
    templateModel.put("formDatas", formDatas);
    templateModel.put("locale", new JadeLocaleHelper(sessionController.getLocale()));
    
    return templateModel;
  }

  private Map<String, Object> createRegistrationMailOrganizerTemplateModel(IllusionEventParticipant participant, IllusionEventParticipant organizer, IllusionEvent illusionEvent, String email, Map<String, String> answers) {
    Map<String, Object> templateModel = new HashMap<>();
    
    User participantUser = participant.getUser();
    User organizerUser = organizer.getUser();
    List<Map<String, Object>> formDatas = createFormDatas(illusionEvent, email, answers, participantUser);
    String participantsUrl = String.format("%s/participants", illusionEventController.getEventUrl(illusionEvent));
    
    templateModel.put("eventName", illusionEvent.getName());
    templateModel.put("formDatas", formDatas);
    templateModel.put("organizerFirstName", organizerUser.getFirstName());
    templateModel.put("participantDisplayName", userController.getUserDisplayName(participantUser));
    templateModel.put("participantsUrl", participantsUrl);
    templateModel.put("locale", new JadeLocaleHelper(sessionController.getLocale()));
    
    return templateModel;
  }

  private List<Map<String,Object>> createFormDatas(IllusionEvent illusionEvent, String email, Map<String, String> answers, User user) {
    List<Map<String, Object>> formDatas = new ArrayList<>();
    IllusionEventRegistrationForm form = illusionEventController.findEventRegistrationForm(illusionEvent);
    
    Locale locale = sessionController.getLocale();
    
    if (form != null) {
      FormReader formReader = new FormReader(form.getData());
      for (String field : formReader.getFields(true)) {
        formDatas.add(createFormData(formReader.getFieldLabel(field), answers.get(field)));
      }
    } else {
      String firstName = user.getFirstName();
      String lastName = user.getLastName();
      
      formDatas.add(createFormData(ExternalLocales.getText(locale, "illusion.registration.registeredMail.defaultEmailHeader"), email)); 
      
      if (StringUtils.isNotBlank(firstName)) {
        formDatas.add(createFormData(ExternalLocales.getText(locale, "illusion.registration.registeredMail.defaultFirstNameHeader"), firstName)); 
      }
      
      if (StringUtils.isNotBlank(lastName)) {
        formDatas.add(createFormData(ExternalLocales.getText(locale, "illusion.registration.registeredMail.defaultLastNameHeader"), lastName)); 
      }
    }
    
    return formDatas;
  }
  
  private Map<String, Object> createFormData(String label, String value) {
    Map<String, Object> data = new HashMap<>();
    data.put("label", label);
    data.put("value", StringUtils.replace(value, "\n", "<br/>"));
    return data;
  }
  
  protected void sendConfirmRegistrationMails(IllusionEvent illusionEvent, IllusionEventParticipant participant, boolean newUser, String password, Map<String, String> answers) throws JadeException, IOException, MessagingException {
    sendConfirmRegistrationMail(illusionEvent, participant, newUser, password, answers);
    sendConfirmRegistrationOrganizerMails(illusionEvent, participant, answers);
  }
  
  protected void sendConfirmRegistrationMail(IllusionEvent illusionEvent, IllusionEventParticipant participant, boolean newUser, String password, Map<String, String> answers) throws JadeException, IOException, MessagingException {
    String email = userController.getUserPrimaryEmail(participant.getUser());
    
    Map<String, Object> templateModel = createRegistrationConfirmMailTemplateModel(participant, illusionEvent, email, newUser, password, answers);
          
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    String toName = getParticipantDisplayName(participant);
    String subject = ExternalLocales.getText(sessionController.getLocale(), "illusion.registration.registeredMail.subject", illusionEvent.getName());
    String content = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/mail-confirm-registration", templateModel);
    mailer.sendMail(fromMail, fromName, email, toName, subject, content, "text/html");
  }
  
  protected void sendConfirmRegistrationOrganizerMails(IllusionEvent illusionEvent, IllusionEventParticipant participant, Map<String, String> answers) throws JadeException, IOException, MessagingException {
    String participantEmail = userController.getUserPrimaryEmail(participant.getUser());
    
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    String subject = ExternalLocales.getText(sessionController.getLocale(), "illusion.registration.registeredMail.subject", illusionEvent.getName());
    JadeConfiguration jadeConfiguration = getJadeConfiguration();
    
    List<IllusionEventParticipant> organizers = illusionEventController.listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.ORGANIZER);
    for (IllusionEventParticipant organizer : organizers) {
      String toName = getParticipantDisplayName(organizer);
      String toEmail = userController.getUserPrimaryEmail(organizer.getUser());
      Map<String, Object> templateModel = createRegistrationMailOrganizerTemplateModel(participant, organizer, illusionEvent, participantEmail, answers);
      String content = jadeController.renderTemplate(jadeConfiguration, illusionEvent.getUrlName() + "/mail-confirm-registration-organizer", templateModel);
      mailer.sendMail(fromMail, fromName, toEmail, toName, subject, content, "text/html");
    }
  }
  
  private Long id;
  private String name;
  private String description;
  private String illusionFolderPath;
  private boolean mayManageEvent;
  private JadeConfiguration jadeConfiguration;
}

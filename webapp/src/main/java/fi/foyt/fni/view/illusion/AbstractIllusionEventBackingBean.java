package fi.foyt.fni.view.illusion;

import javax.inject.Inject;

import org.ocpsoft.rewrite.annotation.RequestAction;

import de.neuland.jade4j.JadeConfiguration;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionJadeTemplateLoader;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
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

  @RequestAction
  public String basicInit() {
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(getUrlName());
    if (illusionEvent == null) {
      return "/error/not-found.jsf";
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
  
  private Long id;
  private String name;
  private String description;
  private String illusionFolderPath;
  private boolean mayManageEvent;
  private JadeConfiguration jadeConfiguration;
}

package fi.foyt.fni.view.illusion;

import javax.inject.Inject;

import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionEventController;
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
  
  private Long id;
  private String name;
  private String description;
  private String illusionFolderPath;
  private boolean mayManageEvent;
}

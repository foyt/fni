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

public abstract class AbstractIllusionEventBackingBean {

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  public String basicInit() {
    IllusionEvent illusionEvent = illusionEventController.findIllusionGroupByUrlName(getUrlName());
    if (illusionEvent == null) {
      return "/error/not-found.jsf";
    }
    
    IllusionEventParticipant member = null;
    
    if (sessionController.isLoggedIn()) {
      User loggedUser = sessionController.getLoggedUser();
  
      member = illusionEventController.findIllusionGroupMemberByUserAndGroup(illusionEvent, loggedUser);
    }
    
    IllusionEventFolder folder = illusionEvent.getFolder();
    
    id = illusionEvent.getId();
    name = illusionEvent.getName();
    description = illusionEvent.getDescription();
    illusionFolderPath = folder.getPath();
    mayManageGroup = member != null ? member.getRole() == IllusionEventParticipantRole.GAMEMASTER : false;
  
    return init(illusionEvent, member);
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
  
  public boolean getMayManageGroup() {
    return mayManageGroup;
  }
  
  private Long id;
  private String name;
  private String description;
  private String illusionFolderPath;
  private boolean mayManageGroup;
}

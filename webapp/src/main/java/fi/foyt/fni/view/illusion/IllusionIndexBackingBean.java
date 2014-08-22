package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/", to = "/illusion/index.jsf")
@LoggedIn
public class IllusionIndexBackingBean {

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;

  @RequestAction
  public String init() {
    User loggedUser = sessionController.getLoggedUser();
    if (loggedUser.getRole() != UserRole.ADMINISTRATOR) {
      return "/index.jsf?faces-redirect=true";
    }
    
    gameMasterGroups = illusionEventController.listIllusionEventsByUserAndRole(loggedUser, IllusionEventParticipantRole.GAMEMASTER);
    playerGroups = illusionEventController.listIllusionEventsByUserAndRole(loggedUser, IllusionEventParticipantRole.PLAYER);
    
    return null;
  }
  
  public List<IllusionEvent> getGameMasterGroups() {
    return gameMasterGroups;
  }
  
  public List<IllusionEvent> getPlayerGroups() {
    return playerGroups;
  }
  
  public Long getGroupPlayerCount(IllusionEvent group) {
    return illusionEventController.countIllusionEventParticipantsByEventAndRole(group, IllusionEventParticipantRole.PLAYER);
  }
  
  private List<IllusionEvent> gameMasterGroups;
  private List<IllusionEvent> playerGroups;
}

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
    
    organizingEvents = illusionEventController.listIllusionEventsByUserAndRole(loggedUser, IllusionEventParticipantRole.ORGANIZER);
    events = illusionEventController.listIllusionEventsByUserAndRole(loggedUser, IllusionEventParticipantRole.PARTICIPANT);
    
    return null;
  }
  
  public List<IllusionEvent> getOrganizingEvents() {
    return organizingEvents;
  }
  
  public List<IllusionEvent> getEvents() {
    return events;
  }
  
  public Long getEventParticipantCount(IllusionEvent group) {
    return illusionEventController.countIllusionEventParticipantsByEventAndRole(group, IllusionEventParticipantRole.PARTICIPANT);
  }
  
  private List<IllusionEvent> organizingEvents;
  private List<IllusionEvent> events;
}

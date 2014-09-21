package fi.foyt.fni.view.illusion;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.illusion.IllusionEventNavigationController.SelectedPage;

@Named
@RequestScoped
@Stateful
public class IllusionEventNavigationBackingBean {
  
  @Inject
  private SessionController sessionController;
  
  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController; 
  
  @PostConstruct
  public void init() {
    indexVisible = true;
    materialsVisible = false;
    participantsVisible = false;
    groupsVisible = false;
    settingsVisible = false;
    
    if (sessionController.isLoggedIn()) {
      User loggedUser = sessionController.getLoggedUser();
      IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(illusionEventNavigationController.getEventUrlName());
      if (illusionEvent != null) {
        IllusionEventParticipant eventParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, loggedUser);
        if (eventParticipant != null) {
          boolean participant = eventParticipant.getRole() == IllusionEventParticipantRole.PARTICIPANT;
          boolean organizer = eventParticipant.getRole() == IllusionEventParticipantRole.ORGANIZER;
          materialsVisible = participant || organizer;
          participantsVisible = organizer;
          groupsVisible = organizer;   
          settingsVisible = organizer;
        }
      }
    }
  }
  
  public boolean getIndexVisible() {
    return indexVisible;
  }

  public boolean getMaterialsVisible() {
    return materialsVisible;
  }

  public boolean getParticipantsVisible() {
    return participantsVisible;
  }

  public boolean getGroupsVisible() {
    return groupsVisible;
  }
  
  public boolean getSettingsVisible() {
    return settingsVisible;
  }
  
  public SelectedPage getSelectedPage() {
    return illusionEventNavigationController.getSelectedPage();
  }
  
  public String getEventUrlName() {
    return illusionEventNavigationController.getEventUrlName();
  }
  
  private boolean indexVisible;
  private boolean materialsVisible;
  private boolean participantsVisible;
  private boolean groupsVisible;
  private boolean settingsVisible;
}

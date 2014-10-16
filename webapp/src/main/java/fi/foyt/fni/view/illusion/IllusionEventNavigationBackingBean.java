package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

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
    
    pages = illusionEventController.listPages();
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
  
  public String getSelectedItem() {
    return illusionEventNavigationController.getSelectedItem();
  }
  
  public String getEventUrlName() {
    return illusionEventNavigationController.getEventUrlName();
  }
  
  public boolean getAdministrationSelected() {
    switch (getSelectedItem()) {
      case "GROUPS":
      case "PARTICIPANTS":
      case "SETTINGS":
        return true;
      default:
        return false;
    }
  }
  
  public List<IllusionEventDocument> getPages() {
    return pages;
  }
  
  public boolean isPageSelected(IllusionEventDocument page) {
    return getSelectedItem().equals("PAGE-" + page.getId().toString());
  }
  
  private boolean indexVisible;
  private boolean materialsVisible;
  private boolean participantsVisible;
  private boolean groupsVisible;
  private boolean settingsVisible;
  private List<IllusionEventDocument> pages; 
}

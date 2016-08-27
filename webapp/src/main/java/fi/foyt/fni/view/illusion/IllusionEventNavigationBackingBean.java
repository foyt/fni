package fi.foyt.fni.view.illusion;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
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
  private IllusionEventPageController illusionEventPagesController;
  
  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;
  
  @PostConstruct
  @TransactionAttribute (TransactionAttributeType.REQUIRES_NEW)
  public void init() {
    administrationVisible = false;
    
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(illusionEventNavigationController.getEventUrlName());

    if (sessionController.isLoggedIn()) {
      User loggedUser = sessionController.getLoggedUser();
      if (illusionEvent != null) {
        IllusionEventParticipant eventParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, loggedUser);
        if (eventParticipant != null) {
          boolean organizer = eventParticipant.getRole() == IllusionEventParticipantRole.ORGANIZER;
          administrationVisible = organizer;
        }
      }
    }

    pages = illusionEventPagesController.listVisiblePages(illusionEvent, sessionController.getLoggedUser());
  }
  
  public boolean getAdministrationVisible() {
    return administrationVisible;
  }
  
  public String getSelectedPage() {
    return illusionEventNavigationController.getSelectedPage();
  }
  
  public String getEventUrlName() {
    return illusionEventNavigationController.getEventUrlName();
  }
  
  public boolean getAdministrationSelected() {
    switch (getSelectedPage()) {
      case "GROUPS":
      case "PARTICIPANTS":
      case "SETTINGS":
      case "MANAGE_PAGES":
      case "MANAGE_TEMPLATES":
        return true;
      default:
        return false;
    }
  }
  
  public List<IllusionEventPage> getPages() {
    return pages;
  }
  
  public boolean isPageSelected(IllusionEventPage page) {
    return getSelectedPage().equals(page.getId());
  }
  
  private boolean administrationVisible;
  private List<IllusionEventPage> pages; 
}

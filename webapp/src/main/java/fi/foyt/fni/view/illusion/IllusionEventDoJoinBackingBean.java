package fi.foyt.fni.view.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/dojoin", to = "/illusion/dojoin.jsf")
public class IllusionEventDoJoinBackingBean {

  @Parameter
  private String urlName;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private SessionController sessionController;
  
  @RequestAction
  @Deferred
  @LoggedIn
  public String init() {
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(getUrlName());
    if (illusionEvent == null) {
      return "/error/not-found.jsf";
    }
    
    User loggedUser = sessionController.getLoggedUser();
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, loggedUser);
    if (participant == null) {
      switch (illusionEvent.getJoinMode()) {
        case APPROVE:
          illusionEventController.createIllusionEventParticipant(loggedUser, illusionEvent, null, IllusionEventParticipantRole.PENDING_APPROVAL);
          FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("illusion.event.approvalPendingMessage"));
          return "/illusion/event.jsf?faces-redirect=true&ignoreMessages=true&urlName=" + getUrlName();
        case OPEN:
          illusionEventController.createIllusionEventParticipant(loggedUser, illusionEvent, null, IllusionEventParticipantRole.PARTICIPANT);
          return "/illusion/event.jsf?faces-redirect=true&urlName=" + getUrlName();
        default:
          return "/error/access-denied.jsf";
      }      
    } else {
      switch (participant.getRole()) {
        case BANNED:
        case BOT:
          return "/error/access-denied.jsf";
        case INVITED:
          if (illusionEvent.getSignUpFee() == null) {
            illusionEventController.updateIllusionEventParticipantRole(participant, IllusionEventParticipantRole.PARTICIPANT);
          } else {
            return "/illusion/event-payment.jsf?faces-redirect=true&urlName=" + getUrlName();
          }
        case PENDING_APPROVAL:
        case WAITING_PAYMENT:
          return "/illusion/event.jsf?faces-redirect=true&urlName=" + getUrlName();
        case ORGANIZER:
        case PARTICIPANT:
          return "/illusion/event.jsf?faces-redirect=true&urlName=" + getUrlName();
      }
    }
    
    return null;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
}
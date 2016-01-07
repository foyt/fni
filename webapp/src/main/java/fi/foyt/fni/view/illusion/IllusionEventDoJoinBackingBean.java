package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Named
@Stateful
@Join (path = "/illusion/event/{urlName}/dojoin", to = "/illusion/dojoin.jsf")
public class IllusionEventDoJoinBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private SessionController sessionController;

  @Inject
  private NavigationController navigationController;
  
  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    IllusionEventRegistrationForm registrationForm = illusionEventController.findEventRegistrationForm(illusionEvent);
    if (registrationForm != null) {
      return String.format("/illusion/event-registration.jsf?faces-redirect=true&urlName=%s", getUrlName());
    }
    
    String requireLogin = navigationController.requireLogin();
    if (StringUtils.isNotBlank(requireLogin)) {
      return requireLogin;
    }
    
    String redirectRule = null;
    boolean newParticipant = false;
    
    User loggedUser = sessionController.getLoggedUser();
    if (participant == null) {
      if (!illusionEvent.getPublished()) {
        return navigationController.accessDenied();
      }
      
      switch (illusionEvent.getJoinMode()) {
        case APPROVE:
          participant = illusionEventController.createIllusionEventParticipant(loggedUser, illusionEvent, null, IllusionEventParticipantRole.PENDING_APPROVAL);
          FacesUtils.addPostRedirectMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("illusion.event.approvalPendingMessage"));
          newParticipant = true;
          redirectRule = "/illusion/event.jsf?faces-redirect=true&ignoreMessages=true&urlName=" + getUrlName();
        break;
        case OPEN:
          participant = illusionEventController.createIllusionEventParticipant(loggedUser, illusionEvent, null, IllusionEventParticipantRole.PARTICIPANT);
          newParticipant = true;
          redirectRule = "/illusion/event.jsf?faces-redirect=true&urlName=" + getUrlName();
        break;
        default:
          return navigationController.accessDenied();
      }      
    } else {
      if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER && !illusionEvent.getPublished()) {
        return navigationController.accessDenied();
      }
      
      switch (participant.getRole()) {
        case BANNED:
        case BOT:
          return navigationController.accessDenied();
        case INVITED:
          if (illusionEvent.getSignUpFee() == null) {
            illusionEventController.updateIllusionEventParticipantRole(participant, IllusionEventParticipantRole.PARTICIPANT);
          } else {
            redirectRule = "/illusion/event-payment.jsf?faces-redirect=true&urlName=" + getUrlName();
          }
        break;
        case PENDING_APPROVAL:
        case WAITING_PAYMENT:
        case ORGANIZER:
        case PARTICIPANT:
          redirectRule = "/illusion/event.jsf?faces-redirect=true&urlName=" + getUrlName();
        break;
      }
    }
    
    if (newParticipant) {
      try {
        sendConfirmRegistrationMails(illusionEvent, participant, false, null, null);
      } catch (JadeException | IOException e) {
        logger.log(Level.SEVERE, "Failed to render registration mail template", e);
        return navigationController.internalError();
      } catch (MessagingException e) {
        logger.log(Level.SEVERE, "Failed to send registration mail", e);
        return navigationController.internalError();
      }
    }
    
    return redirectRule;
  }

  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
}

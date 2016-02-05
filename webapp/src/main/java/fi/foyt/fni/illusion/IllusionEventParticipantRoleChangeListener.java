package fi.foyt.fni.illusion;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.LocaleUtils;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.jade.JadeLocaleHelper;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionEventParticipantRoleChangeListener {
  
  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionMailer illusionMailer;
  
  public void onParticipantRoleChangeEvent(@Observes IllusionParticipantRoleChangeEvent event) {
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(event.getMemberId());
    IllusionEvent illusionEvent = participant.getEvent();
    User user = participant.getUser();
    Locale locale = LocaleUtils.toLocale(user.getLocale());
    
    switch (event.getOldRole()) {
      case PENDING_APPROVAL:
        handleJoinRequestReply(participant, illusionEvent, user, locale);
      break;
      default:
      break;
    }
  }
  
  private void handleJoinRequestReply(IllusionEventParticipant participant, IllusionEvent illusionEvent, User user, Locale locale) {
    String subject = ExternalLocales.getText(locale, "illusion.payment.joinRequestReplyMail.subject", illusionEvent.getName());
    Map<String, Object> templateModel = createJoinRequestReplyMailTemplateModel(participant, illusionEvent);
    illusionMailer.sendMail(participant, subject, "mail-join-request-reply", templateModel);
  }

  private Map<String, Object> createJoinRequestReplyMailTemplateModel(IllusionEventParticipant participant, IllusionEvent illusionEvent) {
    Map<String, Object> templateModel = new HashMap<>();
    User user = participant.getUser();
    IllusionEventParticipantRole role = participant.getRole();
    
    String accessCode = participant.getAccessCode();
    boolean waitingPayment = role == IllusionEventParticipantRole.WAITING_PAYMENT;
    boolean directlyAdded = (role == (IllusionEventParticipantRole.ORGANIZER)) || (role == IllusionEventParticipantRole.PARTICIPANT);
    boolean accepted = waitingPayment || directlyAdded;
    
    Locale locale = LocaleUtils.toLocale(user.getLocale());
    String eventUrl = illusionEventController.getEventUrl(illusionEvent);
    String paymentLink = null;
    
    if (waitingPayment) {
      paymentLink = accessCode != null ? String.format("%s/payment?accessCode=%s", eventUrl, accessCode) : String.format("%s/payment", eventUrl);
    }
    
    templateModel.put("firstName", user.getFirstName());
    templateModel.put("eventName", illusionEvent.getName());
    templateModel.put("eventLink", eventUrl);
    templateModel.put("accepted", accepted);
    templateModel.put("waitingPayment", waitingPayment);
    templateModel.put("paymentLink", paymentLink);
    templateModel.put("locale", new JadeLocaleHelper(locale));
    
    return templateModel;
  }
}

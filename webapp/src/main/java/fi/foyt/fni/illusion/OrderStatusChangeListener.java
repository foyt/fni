package fi.foyt.fni.illusion;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.LocaleUtils;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.OrderPaidEvent;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.jade.JadeLocaleHelper;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;

@ApplicationScoped
public class OrderStatusChangeListener {
	
	@Inject
	private Logger logger;
	
	@Inject
	private OrderController orderController;

  @Inject
  private IllusionMailer illusionMailer;
  
  @Inject
  private UserController userController; 
  
	@Inject
	private IllusionEventController illusionEventController;
  
	public void onOrderPaid(@Observes OrderPaidEvent event) {
		if (event.getOrderId() != null) {
  		Order order = orderController.findOrderById(event.getOrderId()); 
  		if (order != null) {
  		  switch (order.getType()) {
  		    case ILLUSION_EVENT:
  		      IllusionEvent illusionEvent = orderController.findOrderIllusionEvent(order);
  		      if (illusionEvent != null) {
  		        IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, order.getCustomer());
    		      illusionEventController.updateIllusionEventParticipantRole(participant, IllusionEventParticipantRole.PARTICIPANT);
    		      sendPaymentAcceptedMail(participant);
    		      sendPaymentAcceptedMailOrganizers(participant);
  		      } else {
  		        logger.severe("Tried to lift illusion group member role to participant for non-existing group");
  		      }
  		    break;
  		    case GAMELIBRARY_BOOK:
  		    break;
          default:
            logger.severe(String.format("Unknown order type %s", order.getType().toString()));
          break;
  		  }
  		} else {
  			logger.severe("Tried to lift illusion group member role to participant for non-existing order");
  		}
		} else {
      logger.severe("Tried to lift illusion group member role to participant for non-existing order");
		}
	}
	
  private void sendPaymentAcceptedMail(IllusionEventParticipant participant) {
    User user = participant.getUser();
    IllusionEvent illusionEvent = participant.getEvent();
    Locale locale = LocaleUtils.toLocale(user.getLocale());
    
    String subject = ExternalLocales.getText(locale, "illusion.payment.eventPaidMail.subject", illusionEvent.getName());
    Map<String, Object> templateModel = createPaymentAcceptedMailTemplateModel(participant, illusionEvent);
    illusionMailer.sendMail(participant, subject, "mail-event-paid", templateModel);
  }
  
  private void sendPaymentAcceptedMailOrganizers(IllusionEventParticipant participant) {
    IllusionEvent event = participant.getEvent();
    List<IllusionEventParticipant> organizers = illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.ORGANIZER);
    for (IllusionEventParticipant organizer : organizers) {
      Locale locale = LocaleUtils.toLocale(organizer.getUser().getLocale());
      String subject = ExternalLocales.getText(locale, "illusion.payment.eventPaidOrganizerMail.subject", event.getName());
      Map<String, Object> templateModel = createPaymentAcceptedOrganizerMailTemplateModel(participant, organizer, event);
      illusionMailer.sendMail(organizer, subject, "mail-event-paid-organizer", templateModel);
    }
  }
  
  private Map<String, Object> createPaymentAcceptedMailTemplateModel(IllusionEventParticipant participant, IllusionEvent illusionEvent) {
    Map<String, Object> templateModel = new HashMap<>();
    User user = participant.getUser();
    Locale locale = LocaleUtils.toLocale(user.getLocale());
    String eventUrl = illusionEventController.getEventUrl(illusionEvent);
    
    templateModel.put("firstName", user.getFirstName());
    templateModel.put("eventName", illusionEvent.getName());
    templateModel.put("eventLink", eventUrl);
    templateModel.put("locale", new JadeLocaleHelper(locale));
    
    return templateModel;
  }
  private Map<String, Object> createPaymentAcceptedOrganizerMailTemplateModel(IllusionEventParticipant participant, IllusionEventParticipant organizer, IllusionEvent event) {
    Map<String, Object> templateModel = new HashMap<>();
    User participantUser = participant.getUser();
    User organizerUser = participant.getUser();
    Locale locale = LocaleUtils.toLocale(organizerUser.getLocale());
    String eventUrl = illusionEventController.getEventUrl(event);
    String participantName = userController.getUserDisplayName(participantUser);
    
    templateModel.put("organizerFirstName", organizerUser.getFirstName());
    templateModel.put("participantName", participantName);
    templateModel.put("eventName", event.getName());
    templateModel.put("eventLink", eventUrl);
    templateModel.put("locale", new JadeLocaleHelper(locale));
    
    return templateModel;
  }
}

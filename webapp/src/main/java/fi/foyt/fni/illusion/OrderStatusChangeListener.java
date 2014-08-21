package fi.foyt.fni.illusion;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.gamelibrary.OrderController;
import fi.foyt.fni.gamelibrary.OrderEvent;
import fi.foyt.fni.gamelibrary.OrderPaid;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.gamelibrary.Order;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

public class OrderStatusChangeListener {
	
	@Inject
	private Logger logger;
	
	@Inject
	private OrderController orderController;

  @Inject
	private UserController userController;

  @Inject
  private Mailer mailer;
  
	@Inject
	private IllusionGroupController illusionGroupController;

  @Inject
	private SystemSettingsController systemSettingsController;
	
	public void onOrderPaid(@Observes @OrderPaid OrderEvent event) {
		if (event.getOrderId() != null) {
  		Order order = orderController.findOrderById(event.getOrderId()); 
  		if (order != null) {
  		  switch (order.getType()) {
  		    case ILLUSION_GROUP:
  		      IllusionGroup group = orderController.findOrderIllusionGroup(order);
  		      if (group != null) {
  		        IllusionEventParticipant member = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, order.getCustomer());
    		      illusionGroupController.updateIllusionGroupMemberRole(member, IllusionEventParticipantRole.PLAYER);
    		      sendPaymentAcceptedMail(member);
  		      } else {
  		        logger.severe("Tried to lift illusion group member role to player for non-existing group");
  		      }
  		    break;
  		    case GAMELIBRARY_BOOK:
  		    break;
  		  }
  		} else {
  			logger.severe("Tried to lift illusion group member role to player for non-existing order");
  		}
		} else {
      logger.severe("Tried to lift illusion group member role to player for non-existing order");
		}
	}
	
  private void sendPaymentAcceptedMail(IllusionEventParticipant groupMember) {
    User user = groupMember.getUser();
    Locale userLocale = LocaleUtils.toLocale(user.getLocale());
    String userMail = userController.getUserPrimaryEmail(user);
    String userName = groupMember.getUser().getFullName();
    String groupName = groupMember.getGroup().getName();
    String groupUrlName = groupMember.getGroup().getUrlName();
    
    String groupUrl = systemSettingsController.getSiteUrl(false, true);
    if (StringUtils.isNotBlank(groupUrl)) {
      groupUrl += "/illusion/group/" + groupUrlName;
    }

    String subject = ExternalLocales.getText(userLocale, "illusion.mail.paymentAccepted.subject");
    String content = ExternalLocales.getText(userLocale, "illusion.mail.paymentAccepted.content", userName, groupName, groupUrl);

    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    
    try {
      mailer.sendMail(fromMail, fromName, userMail, userName, subject, content, "text/plain");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send a group accept notification mail", e);
    }
  }
}

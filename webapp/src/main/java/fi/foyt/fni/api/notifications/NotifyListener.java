package fi.foyt.fni.api.notifications;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.api.ApiMessages;
import fi.foyt.fni.api.events.FriendRemovedEvent;
import fi.foyt.fni.api.events.FriendRequestConfirmedEvent;
import fi.foyt.fni.api.events.FriendRequestEvent;
import fi.foyt.fni.api.events.MaterialSharedEvent;
import fi.foyt.fni.api.events.PrivateMessageSentEvent;
import fi.foyt.fni.materials.MaterialArchetype;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.messages.MessageController;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.messages.RecipientMessage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.html.HtmlUtils;
import fi.foyt.fni.utils.mail.MailUtils;

public class NotifyListener {
	
	private static final String API_PATH = "v1/";

	@Inject
	private Logger logger;

	@Inject
	private UserController userController;

	@Inject
	private MaterialController materialController;
	
	@Inject
	private MessageController messageController;
	
	@Inject
	private MaterialPermissionController materialPermissionController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	public void onFriendRequest(@Observes FriendRequestEvent event) {
		User friend = event.getFriend();
		User user = event.getUser();

		Locale friendLocale = getUserLocale(friend);
		String userName = user.getFullName();
		String confirmKey = event.getConfirmKey();
		String acceptLink = getApiUrl(event.getUriInfo()) + "friends/confirmFriend/" + confirmKey;

		String subject = ApiMessages.getText(friendLocale, "friends.notifications.friendRequestSubject", userName);
		String content = ApiMessages.getText(friendLocale, "friends.notifications.friendRequestContent", userName, acceptLink);

		if (isNotificationEnabled(friend, "notifications.friendrequest.mail")) {
			try {
				sendEmail(friend, subject, content);
			} catch (MessagingException e) {
				logger.log(Level.SEVERE, "Failed to send an email notification of friend request", e);
			}
		}

		sendMessage(friend, subject, content);
	}

	public void onFriendRequestConfirmed(@Observes FriendRequestConfirmedEvent event) {
		User friend = event.getFriend();
		User user = event.getUser();

		String friendName = friend.getFullName();
		String profileLink = getApplicationUrl(event.getUriInfo()) + "users/" + friend.getId();
		Locale userLocale = getUserLocale(user);
		String subject = ApiMessages.getText(userLocale, "friends.notifications.friendRequestAcceptedSubject", friendName);
		String content = ApiMessages.getText(userLocale, "friends.notifications.friendRequestAcceptedContent", friendName, profileLink);

		if (isNotificationEnabled(user, "notifications.friendrequestaccepted.mail")) {
			try {
				sendEmail(user, subject, content);
			} catch (MessagingException e) {
				logger.log(Level.SEVERE, "Failed to send an email notification of friend request confirmation", e);
			}
		}

		sendMessage(user, subject, content);
	}

	public void onFriendRemoved(@Observes FriendRemovedEvent event) {
		User friend = event.getFriend();
		User user = event.getUser();

		String userName = user.getFullName();
		Locale friendLocale = getUserLocale(friend);
		String subject = ApiMessages.getText(friendLocale, "friends.notifications.removedFromFriendsSubject", userName);
		String content = ApiMessages.getText(friendLocale, "friends.notifications.removedFromFriendsContent", userName);

		if (isNotificationEnabled(friend, "notifications.removedfromfriends.mail")) {
			try {
				sendEmail(friend, subject, content);
			} catch (MessagingException e) {
				logger.log(Level.SEVERE, "Failed to send an email notification of friend removal", e);
			}
		}

		sendMessage(friend, subject, content);
	}

	public void onPrivateMessageSend(@Observes PrivateMessageSentEvent event) {
		RecipientMessage recipientMessage = event.getRecipientMessage();
		User recipient = recipientMessage.getRecipient();

		if (isNotificationEnabled(recipient, "notifications.privatemessage.mail")) {
			Locale recipientLocale = getUserLocale(recipient);
			fi.foyt.fni.persistence.model.messages.Message msg = recipientMessage.getMessage();
			User sender = msg.getSender();
			String senderName = sender.getFullName();
			String messageContent = StringUtils.abbreviate(HtmlUtils.htmlToPlainText(msg.getContent()), 100);
			String messageLink = getApplicationUrl(event.getUriInfo()) + "?a=viewmessage&ap=messageId:" + msg.getId();

			String subject = ApiMessages.getText(recipientLocale, "messages.notifications.privateMessageRecievedSubject", senderName);
			String content = ApiMessages.getText(recipientLocale, "messages.notifications.privateMessageRecievedContent", senderName, messageContent, messageLink);

			try {
				sendEmail(recipient, subject, content);
			} catch (MessagingException e) {
				logger.log(Level.SEVERE, "Failed to send an email notification of a private message", e);
			}
		}
	}

	public void onMaterialShared(@Observes MaterialSharedEvent event) {
		User sharingUser = event.getSharingUser();
		User targetUser = event.getTargetUser();
		Material material = event.getMaterial();
		MaterialRole role = materialPermissionController.getUserMaterialRole(targetUser, material);
		String action = null;
		
		switch (role) {
			case MAY_EDIT:
				MaterialArchetype materialArchetype = materialController.getMaterialArchetype(material);
				if (materialController.isEditableType(material.getType()) == false || materialArchetype == MaterialArchetype.FOLDER) {
					action = "viewmaterial";
				} else {
					action = "editmaterial";
				}
			break;
			case MAY_VIEW:
				action = "viewmaterial";
		  break;
		}

		String sharingUserName = sharingUser.getFullName();
		String materialName = material.getTitle();
		String materialLink = getApplicationUrl(event.getUriInfo()) + "forge/?a=" + action + "&ap=materialId:" + material.getId();

		Locale targetUserLocale = getUserLocale(targetUser);
		String subject = ApiMessages.getText(targetUserLocale, "materials.all.notifications.materialSharedSubject", sharingUserName);
		String content = ApiMessages.getText(targetUserLocale, "materials.all.notifications.materialSharedContent", sharingUserName, materialName, materialLink);

		if (isNotificationEnabled(targetUser, "notifications.materialshared.mail")) {
			try {
				sendEmail(targetUser, subject, content);
			} catch (MessagingException e) {
				logger.log(Level.SEVERE, "Failed to send an email notification of material sharing", e);
			}
		}

		sendMessage(targetUser, subject, content);
	}
	
	private String getApplicationUrl(UriInfo uriInfo) {
		String apiUrl = getApiUrl(uriInfo);
    return apiUrl.substring(0, apiUrl.length() - API_PATH.length());
  }

	private String getApiUrl(UriInfo uriInfo) {
		return uriInfo.getBaseUri().toString();
	}

	private Locale getUserLocale(User user) {
		String locale = user.getLocale();
		if (StringUtils.isBlank(locale)) {
			return Locale.getDefault();
		}

		String[] localeArr = locale.split("_");
		if (localeArr.length == 1) {
			return new Locale(localeArr[0]);
		} else if (localeArr.length == 2) {
			return new Locale(localeArr[0], localeArr[1]);
		} else {
			return new Locale(localeArr[0], localeArr[1], localeArr[2]);
		}
	}

	private void sendEmail(User recipient, String subject, String content) throws MessagingException {
		String recipientEmail = userController.getUserPrimaryEmail(recipient);

		if (StringUtils.isNotBlank(recipientEmail)) {
			String systemMail = systemSettingsController.getSetting("system.mailer.mail");
			String systemName = systemSettingsController.getSetting("system.mailer.name");
			String recipientName = recipient.getFullName();
			MailUtils.sendMail(systemMail, systemName, recipientEmail, recipientName, subject, content, "text/html");
		}
	}

	private void sendMessage(User recipient, String subject, String content) {
		String systemMail = systemSettingsController.getSetting("system.mailer.mail");
		User systemUser = userController.findUserByEmail(systemMail);
		messageController.sendMessage(systemUser, recipient, subject, content);
	}

	private boolean isNotificationEnabled(User user, String notification) {
		return "1".equals(userController.getUserSettingValue(user, notification));
	}
}

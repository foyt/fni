package fi.foyt.fni.illusion;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

public class IllusionGroupRoleChangeListener {
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private UserController userController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private Mailer mailer;
  
  public void onMemberAddedEvent(@Observes MemberAddedEvent event) {
    IllusionEventParticipant groupMember = illusionGroupController.findIllusionGroupMemberById(event.getMemberId());
    if (groupMember.getRole() == IllusionEventParticipantRole.PENDING_APPROVAL) {
      List<IllusionEventParticipant> gamemasters = illusionGroupController.listIllusionGroupMembersByGroupAndRole(groupMember.getGroup(), IllusionEventParticipantRole.GAMEMASTER);
      String groupUrl = systemSettingsController.getSiteUrl(false, true);
      if (StringUtils.isNotBlank(groupUrl)) {
        groupUrl += "/illusion/group/" + groupMember.getGroup().getUrlName();
      }
      
      for (IllusionEventParticipant gamemaster : gamemasters) {
        sendGroupJoinRequestMail(groupUrl, groupMember, gamemaster);
      }
    }
  }

  public void onMemberRoleChangeEvent(@Observes MemberRoleChangeEvent event) {
    if (event.getOldRole().equals(IllusionEventParticipantRole.PENDING_APPROVAL)) {
      IllusionEventParticipant groupMember = illusionGroupController.findIllusionGroupMemberById(event.getMemberId());
      
      switch (event.getNewRole()) {
        case BANNED:
          sendDeclineMail(groupMember);
        break;
        case GAMEMASTER:
        case PLAYER:
          sendAcceptMail(groupMember);
        break;
        case WAITING_PAYMENT:
          sendPaidGroupAcceptMail(groupMember);
        default:
        break;
      }
    }
  }
  
  private void sendPaidGroupAcceptMail(IllusionEventParticipant groupMember) {
    User user = groupMember.getUser();
    Locale userLocale = LocaleUtils.toLocale(user.getLocale());
    String userMail = userController.getUserPrimaryEmail(user);
    String userName = groupMember.getUser().getFullName();
    String groupName = groupMember.getGroup().getName();
    String groupUrlName = groupMember.getGroup().getUrlName();
    
    String paymentUrl = systemSettingsController.getSiteUrl(false, true);
    if (StringUtils.isNotBlank(paymentUrl)) {
      paymentUrl += "/illusion/group/" + groupUrlName + "/payment";
    }

    String subject = ExternalLocales.getText(userLocale, "illusion.mail.paidGroupJoinRequestAccepted.subject");
    String content = ExternalLocales.getText(userLocale, "illusion.mail.paidGroupJoinRequestAccepted.content", userName, groupName, paymentUrl);

    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    
    try {
      mailer.sendMail(fromMail, fromName, userMail, userName, subject, content, "text/plain");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send a group accept notification mail", e);
    }
  }

  private void sendGroupJoinRequestMail(String groupUrl, IllusionEventParticipant groupMember, IllusionEventParticipant gamemaster) {
    String groupName = groupMember.getGroup().getName();

    User master = gamemaster.getUser();
    Locale masterLocale = LocaleUtils.toLocale(master.getLocale());
    String masterMail = userController.getUserPrimaryEmail(master);
    String masterName = master.getFullName();
    String membersUrl = groupUrl + "/members";

    User user = groupMember.getUser();
    String userName = user.getFullName();
    
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
 
    String subject = ExternalLocales.getText(masterLocale, "illusion.mail.joinRequest.subject");
    String content = ExternalLocales.getText(masterLocale, "illusion.mail.joinRequest.content", masterName, userName, groupName, membersUrl);  

    try {
      mailer.sendMail(fromMail, fromName, masterMail, masterName, subject, content, "text/plain");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send a group accept notification mail", e);
    }
  }
  
  private void sendAcceptMail(IllusionEventParticipant groupMember) {
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

    String subject = ExternalLocales.getText(userLocale, "illusion.mail.joinRequestAccepted.subject");
    String content = ExternalLocales.getText(userLocale, "illusion.mail.joinRequestAccepted.content", userName, groupName, groupUrl);

    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    
    try {
      mailer.sendMail(fromMail, fromName, userMail, userName, subject, content, "text/plain");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send a group accept notification mail", e);
    }
  }
  
  private void sendDeclineMail(IllusionEventParticipant groupMember) {
    User user = groupMember.getUser();
    Locale userLocale = LocaleUtils.toLocale(user.getLocale());
    String userMail = userController.getUserPrimaryEmail(user);
    String userName = groupMember.getUser().getFullName();
    String groupName = groupMember.getGroup().getName();
    
    String subject = ExternalLocales.getText(userLocale, "illusion.mail.joinRequestDeclined.subject");
    String content = ExternalLocales.getText(userLocale, "illusion.mail.joinRequestDeclined.content", userName, groupName);

    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    
    try {
      mailer.sendMail(fromMail, fromName, userMail, userName, subject, content, "text/plain");
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send a group accept notification mail", e);
    }
  }
  
}

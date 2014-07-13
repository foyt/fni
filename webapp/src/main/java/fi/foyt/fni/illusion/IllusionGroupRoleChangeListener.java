package fi.foyt.fni.illusion;

import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.LocaleUtils;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
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
    IllusionGroupMember groupMember = illusionGroupController.findIllusionGroupMemberById(event.getMemberId());
    if (groupMember.getRole() == IllusionGroupMemberRole.PENDING_APPROVAL) {
      List<IllusionGroupMember> gamemasters = illusionGroupController.listIllusionGroupMembersByGroupAndRole(groupMember.getGroup(), IllusionGroupMemberRole.GAMEMASTER);
      for (IllusionGroupMember gamemaster : gamemasters) {
        sendGroupJoinRequestMail(event.getGroupUrl(), groupMember, gamemaster);
      }
    }
  }

  public void onMemberRoleChangeEvent(@Observes MemberRoleChangeEvent event) {
    if (event.getOldRole().equals(IllusionGroupMemberRole.PENDING_APPROVAL)) {
      IllusionGroupMember groupMember = illusionGroupController.findIllusionGroupMemberById(event.getMemberId());
      
      switch (event.getNewRole()) {
        case BANNED:
          sendDeclineMail(groupMember);
        break;
        case GAMEMASTER:
        case PLAYER:
          sendAcceptMail(event.getGroupUrl(), groupMember);
        break;
        default:
        break;
      }
    }
  }
  
  private void sendGroupJoinRequestMail(String groupUrl, IllusionGroupMember groupMember, IllusionGroupMember gamemaster) {
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
  
  private void sendAcceptMail(String groupUrl, IllusionGroupMember groupMember) {
    User user = groupMember.getUser();
    Locale userLocale = LocaleUtils.toLocale(user.getLocale());
    String userMail = userController.getUserPrimaryEmail(user);
    String userName = groupMember.getUser().getFullName();
    String groupName = groupMember.getGroup().getName();
    
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
  
  private void sendDeclineMail(IllusionGroupMember groupMember) {
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

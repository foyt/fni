package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.mail.Mailer.MailBuilder;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserGroupMember;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/eventMail/*", name = "illusion-eventmail")
@Transactional
public class IllusionEventMailServlet extends AbstractFileServlet {

  private static final long serialVersionUID = 8840385463120576014L;

  @Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private Logger logger;

  @Inject
  private Mailer mailer;
  
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String pathInfo = request.getPathInfo();
    if (StringUtils.isBlank(pathInfo)) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String[] pathItems = StringUtils.removeStart(pathInfo, "/").split("/");
    if (pathItems.length != 1) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String eventUrlName = pathItems[0];
    if (StringUtils.isBlank(eventUrlName)) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    if (!sessionController.isLoggedIn()) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(eventUrlName);
    if (event == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    IllusionEventParticipant loggedParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, loggedUser);
    if ((loggedParticipant == null)||(loggedParticipant.getRole() != IllusionEventParticipantRole.ORGANIZER)) {
      sendError(response, HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    String mailSubject = request.getParameter("mailSubject");
    String mailContent = request.getParameter("mailContent");
    
    List<String> emails = getRecipients(event, request.getParameter("recipients"));
    if (!emails.isEmpty()) {
      sendMails(loggedUser, event, emails, mailSubject, mailContent);
    }
    
	  response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

  private void sendMails(User loggedUser, IllusionEvent event, List<String> emails, String emailSubject, String emailContent) {  
    String fromPersonal = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromAddress = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    String replyToPersonal = loggedUser.getFullName();
    String replyToMail = userController.getUserPrimaryEmail(loggedUser);
    
    try {
      MailBuilder mailBuilder = mailer.getBuilder()
        .setFrom(fromAddress, fromPersonal)
        .setReplyTo(replyToMail, replyToPersonal)
        .setContent(emailContent)
        .setSubject(emailSubject);

      for (String email : emails) {
        mailBuilder.addBcc(email);
      }

      mailBuilder.send();
    } catch (MessagingException | UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, "Could not send a mail", e);
    }  
  }
  
  private List<String> getRecipients(IllusionEvent event, String recipients) {
    List<IllusionEventParticipant> participants = new ArrayList<>();
    
    switch (recipients) {
      case "PARTICIPANTS":
        participants.addAll(illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.PARTICIPANT));
      break;
      case "ORGANIZERS":
        participants.addAll(illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.ORGANIZER));
      break;
      case "INVITED":
        participants.addAll(illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.INVITED));
      break;
      case "PARTICIPANTS_ORGANIZERS_INVITED":
        participants.addAll(illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.PARTICIPANT));
        participants.addAll(illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.ORGANIZER));
        participants.addAll(illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.INVITED));
      break;
    }
    
    if (StringUtils.startsWith(recipients, "GROUP_")) {
      Long groupId = NumberUtils.createLong(recipients.substring(6));
      IllusionEventGroup eventGroup = illusionEventController.findGroupById(groupId);
      if (eventGroup != null) {
        List<UserGroupMember> members = illusionEventController.listGroupMembers(eventGroup);
        for (UserGroupMember member : members) {
          IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(eventGroup.getEvent(), member.getUser());
          if (participant != null) {
            participants.add(participant);
          } else {
            logger.warning(String.format("User %d of group %d is not a member of an event %d", member.getUser().getId(), eventGroup.getId(), eventGroup.getEvent().getId()));
          }
        }
      }
    }
    
    List<String> emails = new ArrayList<>();
    
    for (IllusionEventParticipant participant : participants) {
      String email = userController.getUserPrimaryEmail(participant.getUser());
      if (!emails.contains(email)) {
        emails.add(email);
      }
    }
    
    return emails;
  }

}

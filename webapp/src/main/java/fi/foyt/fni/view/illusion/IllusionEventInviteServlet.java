package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/eventInvite/*", name = "illusion-eventinvite")
@Transactional
public class IllusionEventInviteServlet extends AbstractFileServlet {

  private static final long serialVersionUID = 8840385463120576014L;

  @Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private AuthenticationController authenticationController;

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
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String[] pathItems = StringUtils.removeStart(pathInfo, "/").split("/");
    if (pathItems.length != 1) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String eventUrlName = pathItems[0];
    if (StringUtils.isBlank(eventUrlName)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    if (!sessionController.isLoggedIn()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(eventUrlName);
    if (event == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    IllusionEventParticipant loggedParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, loggedUser);
    if ((loggedParticipant == null)||(loggedParticipant.getRole() != IllusionEventParticipantRole.GAMEMASTER)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    String mailSubject = request.getParameter("mailSubject");
    String mailContent = request.getParameter("mailContent");
    
    String[] emails = request.getParameterValues("email");
    for (String email : emails) {
      inviteUser(event, email, mailSubject, mailContent);
    }

	  response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

  private void inviteUser(IllusionEvent event, String email, String emailSubject, String templateContent) {  
    Date now = new Date();
    String temporaryAccount = "";
    
    User user = userController.findUserByEmail(email);
    if (user == null) {
      user = userController.createUser(null, null, null, sessionController.getLocale(), now, UserProfileImageSource.GRAVATAR);
      userController.createUserEmail(user, email, Boolean.TRUE);
      String password = RandomStringUtils.randomAlphabetic(5);
      String passwordEncoded = DigestUtils.md5Hex(password);
      InternalAuth internalAuth = authenticationController.createInternalAuth(user, passwordEncoded);
      // TODO: verification
      authenticationController.verifyInternalAuth(internalAuth);
      temporaryAccount = ExternalLocales.getText(sessionController.getLocale(), "illusion.mail.temporaryAccount", password);
    }
    
    IllusionEventParticipant illusionEventParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
    if (illusionEventParticipant == null) {
      illusionEventController.createIllusionEventParticipant(user, event, null, IllusionEventParticipantRole.INVITED);
      String emailContent = templateContent.replace("[[LOGIN_INFO]]", temporaryAccount);
      String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
      String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
      
      try {
        mailer.sendMail(fromMail, fromName, email, user.getFullName(), emailSubject, emailContent, "text/plain");
      } catch (MessagingException e) {
        logger.log(Level.SEVERE, "Could not send a event invite mail", e);
      }    
    }

  }

}

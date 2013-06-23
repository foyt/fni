package fi.foyt.fni.view.forge;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.scribe.model.Token;

import fi.foyt.fni.auth.DropboxAuthenticationStrategy;
import fi.foyt.fni.dropbox.DropboxManager;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.messages.MessageController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.materials.DropboxRootFolderDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class DropboxConnectViewController extends AbstractViewController {

	@Inject
	private MessageController messageController;

	@Inject
	private UserController userController;

	@Inject
	private SystemSettingsController systemSettingsController;

  @Inject
  private SessionController sessionController;
  
  @Inject
  private DropboxManager dropboxManager;
  
  @Inject
  private MaterialController materialController;
  
  @Inject
  @DAO
  private UserIdentifierDAO userIdentifierDAO;
  
  @Inject
  @DAO
  private UserTokenDAO userTokenDAO;
  
  @Inject
  @DAO
  private DropboxRootFolderDAO dropboxRootFolderDAO;
  
  @Inject
  private DropboxAuthenticationStrategy dropboxAuthenticationStrategy;
  
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return sessionController.isLoggedIn();
  }

  @Override
  public void execute(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    Locale locale = context.getRequest().getLocale();
    Token dropboxToken = dropboxManager.getDropboxToken(loggedUser);
    
    if (dropboxToken == null) {
      String redirectUrl = context.getBasePath() + "/forge/dropboxconnect.page";
      try {
        context.setRedirect(context.getBasePath() + "/login?loginMethod=DROPBOX&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8"), false);
      } catch (UnsupportedEncodingException e) {
        throw new ViewControllerException(Locales.getText(locale, "generic.error.configurationError"), e);
      }
    } else {
      DropboxRootFolder dropboxRootFolder = dropboxRootFolderDAO.findByUser(loggedUser);
      if (dropboxRootFolder == null) {
        String title = Locales.getText(locale, "forge.dropboxConnect.rootFolderName");
        String urlName = materialController.getUniqueMaterialUrlName(loggedUser, null, null, title);
        Date now = new Date();
        dropboxRootFolder = dropboxRootFolderDAO.create(loggedUser, now, loggedUser, now, null, urlName, title, MaterialPublicity.PRIVATE, null, null);
        
        String viewFolderLink = context.getBasePath() + "/forge/?a=viewmaterial&ap=materialId:" + dropboxRootFolder.getId();
        
        String notificationSubject = Locales.getText(locale, "forge.dropboxConnect.connectedMessageSubject");
        String notificationContent = Locales.getText(locale, "forge.dropboxConnect.connectedMessageContent", viewFolderLink);

        sendMessage(loggedUser, notificationSubject, notificationContent);  
      }

      context.setRedirect(context.getBasePath() + "/forge", false);
    }
  }
  
  private void sendMessage(User recipient, String subject, String content) {
    String systemMail = systemSettingsController.getSetting("system.mailer.mail");
    User systemUser = userController.findUserByEmail(systemMail);
    messageController.sendMessage(systemUser, recipient, subject, content);
  }
}
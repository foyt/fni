package fi.foyt.fni.view.forge;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.scribe.model.Token;
import fi.foyt.fni.auth.UbuntuOneAuthenticationStrategy;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.messages.MessageController;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.materials.UbuntuOneRootFolderDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.materials.UbuntuOneRootFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.ubuntuone.UbuntuOneManager;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class UbuntuOneConnectViewController extends AbstractViewController {
	
	@Inject
	private MessageController messageController;

	@Inject
	private UserController userController;
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
  @Inject
  private SessionController sessionController;
  
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
  private UbuntuOneRootFolderDAO ubuntuOneRootFolderDAO;
  
  @Inject
  private UbuntuOneAuthenticationStrategy ubuntuOneAuthenticationStrategy;
  
  @Inject
  private UbuntuOneManager ubuntuOneManager;
  
  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return sessionController.isLoggedIn();
  }

  @Override
  public void execute(ViewControllerContext context) {
    User loggedUser = sessionController.getLoggedUser();
    Locale locale = context.getRequest().getLocale();
    Token ubuntuOneToken = ubuntuOneManager.getUbuntuOneToken(loggedUser);
    
    if (ubuntuOneToken == null) {
      String redirectUrl = context.getBasePath() + "/forge/ubuntuoneconnect.page";
      
      try {
        context.setRedirect(context.getBasePath() + "/login?loginMethod=UBUNTU_ONE&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8"), false);
      } catch (UnsupportedEncodingException e) {
        throw new ViewControllerException(Locales.getText(locale, "generic.error.configurationError"), e);
      }
    } else {
      UbuntuOneRootFolder ubuntuOneRootFolder = ubuntuOneRootFolderDAO.findByUser(loggedUser);
      if (ubuntuOneRootFolder == null) {
        String title = Locales.getText(locale, "forge.ubuntuOneConnect.rootFolderName");
        String urlName = materialController.getUniqueMaterialUrlName(loggedUser, null, null, title);
        Date now = new Date();
        ubuntuOneRootFolder = ubuntuOneRootFolderDAO.create(loggedUser, now, loggedUser, now, null, urlName, title, MaterialPublicity.PRIVATE, 0l, null);

        String viewFolderLink = context.getBasePath() + "/forge/?a=viewmaterial&ap=materialId:" + ubuntuOneRootFolder.getId();
        
        String notificationSubject = Locales.getText(locale, "forge.ubuntuOneConnect.connectedMessageSubject");
        String notificationContent = Locales.getText(locale, "forge.ubuntuOneConnect.connectedMessageContent", viewFolderLink);

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
package fi.foyt.fni.view;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.auth.AuthenticationStrategy;
import fi.foyt.fni.auth.AuthenticationStrategyManager;
import fi.foyt.fni.chat.ChatController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
public class EditProfileViewBackingBean extends AbstractViewBackingBean {

	@Inject
	private Logger logger;
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;
	
	@Inject
	private AuthenticationController authenticationController;
	
	@Inject
	private MaterialController materialController;
	
	@Inject
	private ChatController chatController;
	
	@Inject
	private AuthenticationStrategyManager authenticationStrategyManager;
	
  // TODO: Requires logged user
	
	public long getMaterialsTotalSize() {
		return materialController.getUserMaterialsTotalSize(sessionController.getLoggedUser());
	}

	public String getMaterialsTotalSizeString() {
		return FileUtils.byteCountToDisplaySize(getMaterialsTotalSize());
	}
	
	public long getMaterialsQuotaSize() {
		return materialController.getUserQuota();
	}

	public String getMaterialsQuotaSizeString() {
		return FileUtils.byteCountToDisplaySize(getMaterialsQuotaSize());
	}
	
	public double getMaterialsQuotaPercent() {
		return ((((double) getMaterialsTotalSize()) / ((double) getMaterialsQuotaSize())) * 100.0);
	}
	
	public String getMaterialsQuotaPercentString() {
		return PERCENT_FORMAT.format(getMaterialsQuotaPercent());
	}
		
//TODO: Requires logged user
	public List<AuthSource> getAddebleAuthSources() {
	  // List all auth sources
		List<AuthSource> authSources = new ArrayList<AuthSource>(Arrays.asList(AuthSource.values()));
		
		// Remove GUEST strategy because it cannot be used for user identification
		authSources.remove(AuthSource.GUEST);
		
		// Remove all sources that do not support logging in 
	  List<AuthSource> removeAuthSources = new ArrayList<AuthSource>();
		for (AuthSource authSource : authSources) {
		  AuthenticationStrategy authenticationStrategy = authenticationStrategyManager.getStrategy(authSource);
			if (!authenticationStrategy.getSupportLogin()) {
			  removeAuthSources.add(authSource);
			}
	  }
			
		for (AuthSource removeAuthSource : removeAuthSources) {
		  authSources.remove(removeAuthSource);
	  }
		
		if (authenticationController.findInternalAuthByUser(sessionController.getLoggedUser()) != null) {
			// If user already has a internal auth, we remove it from the list
			authSources.remove(AuthSource.INTERNAL);
		}
		
		return authSources;
	}
	
	public String removeUserIdentifier(UserIdentifier userIdentifier) {
		authenticationController.removeUserIdentifier(userIdentifier);
		return "/editprofile.jsf";
	}
	
	public List<UserIdentifierInfoBean> getUserIdentifierInfos() {
		List<UserIdentifierInfoBean> result = new ArrayList<>();
		
		List<UserIdentifier> userIdentifiers = authenticationController.listUserIdentifiers(sessionController.getLoggedUser());
		for (UserIdentifier identifier : userIdentifiers) {
			result.add(createUserIdentifierInfoBean(identifier));
		}
		
		return result;
	}
	
	public boolean getFriendRequestNotify() {
		return "1".equals(userController.getUserSettingValue(sessionController.getLoggedUser(), "notifications.friendrequest.mail"));
	}
	
  public void setFriendRequestNotify(boolean value) {
  	userController.setUserSettingValue(sessionController.getLoggedUser(), "notifications.friendrequest.mail", value ? "1" : "0");
  }

	public boolean getFriendRequestAcceptedNotify() {
		return "1".equals(userController.getUserSettingValue(sessionController.getLoggedUser(), "notifications.friendrequestaccepted.mail"));
	}
	
  public void setFriendRequestAcceptedNotify(boolean value) {
  	userController.setUserSettingValue(sessionController.getLoggedUser(), "notifications.friendrequestaccepted.mail", value ? "1" : "0");
  }
	
	public boolean getRemovedFromFriendsNotify() {
		return "1".equals(userController.getUserSettingValue(sessionController.getLoggedUser(), "notifications.removedfromfriends.mail"));
	}
	
	public boolean getMaterialSharedNotify() {
		return "1".equals(userController.getUserSettingValue(sessionController.getLoggedUser(), "notifications.materialshared.mail"));
	}
	
	public boolean getPrivateMessageReceivedNotify() {
		return "1".equals(userController.getUserSettingValue(sessionController.getLoggedUser(), "notifications.privatemessage.mail"));
	}
	
  // TODO: Requires logged user
	public String getChatCredentialsUserJid() {
		UserChatCredentials chatCredentials = getChatCredentials();
		if (chatCredentials != null) {
		  try {
				return chatController.getCredentialsUserJid(chatCredentials);
			} catch (GeneralSecurityException e) {
				logger.log(Level.SEVERE, "Chat credentials decryption failed", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Chat credentials decryption failed", e);
			}
		}
		
		return null;
	}
	
  // TODO: Requires logged user
	public String getChatCredentialsPassword() {
		UserChatCredentials chatCredentials = getChatCredentials();
		if (chatCredentials != null) {
		  try {
				return chatController.getCredentialsPassword(chatCredentials);
			} catch (GeneralSecurityException e) {
				logger.log(Level.SEVERE, "Chat credentials decryption failed", e);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Chat credentials decryption failed", e);
			}
		}
		
		return null;
	}
	
	public String getChatServerUrl() {
		return systemSettingsController.getSetting("chat.xmppServerUrl");
	}
	
	private UserChatCredentials getChatCredentials() {
		return chatController.findUserChatCredentials(sessionController.getLoggedUser());
	}

	private UserIdentifierInfoBean createUserIdentifierInfoBean(UserIdentifier userIdentifier) {
		FacesContext facesContext = getFacesContext();
		ResourceBundle locales = getLocales(facesContext);

		switch (userIdentifier.getAuthSource()) {
			case INTERNAL:
				InternalAuth internalAuth = authenticationController.findInternalAuthByUser(userIdentifier.getUser());
				
				return new UserIdentifierInfoBean(
						userIdentifier, 
						getLocalizedValue(locales, "root.editProfile.authSourceInternalTitle"), 
						true, 
						internalAuth != null ? internalAuth.getVerified() : false,
						"enabledAuthSourceInternal");
			case GOOGLE:
				return new UserIdentifierInfoBean(
						userIdentifier, 
						getLocalizedValue(locales, "root.editProfile.authSourceGoogleTitle"), 
						false,  
						true,
						"enabledAuthSourceGoogle");
			case YAHOO:
				return new UserIdentifierInfoBean(
						userIdentifier, 
						getLocalizedValue(locales, "root.editProfile.authSourceYahooTitle"), 
						false,  
						true,
						"enabledAuthSourceYahoo");
			case FACEBOOK:
				return new UserIdentifierInfoBean(
						userIdentifier, 
						getLocalizedValue(locales, "root.editProfile.authSourceFacebookTitle"), 
						false,  
						true,
						"enabledAuthSourceFacebook");
			case DROPBOX:
				return new UserIdentifierInfoBean(
						userIdentifier, 
						getLocalizedValue(locales, "root.editProfile.authSourceDropboxTitle"), 
						false,  
						true,
						"enabledAuthSourceDropbox");
			case UBUNTU_ONE:
				return new UserIdentifierInfoBean(
						userIdentifier, 
						getLocalizedValue(locales, "root.editProfile.authSourceUbuntuOneTitle"), 
						false, 
						true,
						"enabledAuthSourceUbuntuOne");
			case GUEST:
				return null;
		}
		
		return null;
	}
	
	private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##");

	public class UserIdentifierInfoBean {
		
		public UserIdentifierInfoBean(UserIdentifier identifier, String localizedName, Boolean supportsPasswordChange, Boolean verified, String className) {
			this.identifier = identifier;
			this.localizedName = localizedName;
			this.supportsPasswordChange = supportsPasswordChange;
			this.verified = verified;
			this.className = className;
		}
		
		public String getClassName() {
			return className;
		}
		
		public void setClassName(String className) {
			this.className = className;
		}
		
		public String getLocalizedName() {
			return localizedName;
		}
		
		public void setLocalizedName(String localizedName) {
			this.localizedName = localizedName;
		}

		public Boolean getSupportsPasswordChange() {
			return supportsPasswordChange;
		}
		
		public void setSupportsPasswordChange(Boolean supportsPasswordChange) {
			this.supportsPasswordChange = supportsPasswordChange;
		}
		
		public Boolean getVerified() {
			return verified;
		}
		
		public void setVerified(Boolean verified) {
			this.verified = verified;
		}
		
		public UserIdentifier getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(UserIdentifier identifier) {
			this.identifier = identifier;
		}

		private String localizedName;
		private Boolean supportsPasswordChange;
		private Boolean verified;
		private String className;
		private UserIdentifier identifier;
	}
	
}

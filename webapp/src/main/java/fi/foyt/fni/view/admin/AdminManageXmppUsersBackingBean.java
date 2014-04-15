package fi.foyt.fni.view.admin;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "admin-create-xmpp-users", 
		pattern = "/admin/manage-xmpp-users/", 
		viewId = "/admin/manage-xmpp-users.jsf"
  )
})
public class AdminManageXmppUsersBackingBean {
  
  @Inject
  private UserController userController;

  @Inject
  private ChatCredentialsController chatCredentialsController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
	@URLAction
	@LoggedIn
	@Secure (Permission.SYSTEM_ADMINISTRATION)
	public void load() throws GeneralSecurityException, IOException {
	  users = userController.listUsers();
	  xmppBoshService = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOSH_SERVICE);
	  xmppDomain = systemSettingsController.getSetting(SystemSettingKey.CHAT_DOMAIN);
	  existingUserJids = chatCredentialsController.getAllUserJids();
	  
	  generatedUserJids = new HashMap<>();
	  generatedPasswords = new HashMap<>();
	  
	  for (User user : users) {
	    if (StringUtils.isBlank(getXmppUserJid(user))) {
	      generatedUserJids.put(user.getId(), generateUniqueXmppUserJid(user));
	      generatedPasswords.put(user.getId(), generateXmppPassword());
	    }
 	  }
	}

	@LoggedIn
  @Secure (Permission.SYSTEM_ADMINISTRATION)
	public List<User> getUsers() {
    return users;
  }
  
  @LoggedIn
  @Secure (Permission.SYSTEM_ADMINISTRATION)
  public String getUserEmail(User user) {
    return userController.getUserPrimaryEmail(user);
  }

  public String getGeneratedXmppUserJid(User user) {
    return generatedUserJids.get(user.getId());
  }

  public String getGeneratedXmppPassword(User user) {
    return generatedPasswords.get(user.getId());
  }
  
  @LoggedIn
  @Secure (Permission.SYSTEM_ADMINISTRATION)
  public String getXmppUserJid(User user)  {
    return chatCredentialsController.getUserJidByUser(user);
  }
	
  @LoggedIn
  @Secure (Permission.SYSTEM_ADMINISTRATION)
  public String getXmppUserPassword(User user) throws GeneralSecurityException, IOException {
    return chatCredentialsController.getPasswordByUser(user);
  }
  
  @LoggedIn
  @Secure (Permission.SYSTEM_ADMINISTRATION)
  public boolean hasXmppUserJid(User user) {
    String userJid = getXmppUserJid(user);
    return existingUserJids.contains(userJid);
  }
  
  public String getXmppBoshService() {
    return xmppBoshService;
  }
  
  public String getXmppDomain() {
    return xmppDomain;
  }
  
  public Long getRegisteredUserId() {
    return registeredUserId;
  }
  
  public void setRegisteredUserId(Long registeredUserId) {
    this.registeredUserId = registeredUserId;
  }
  
  public String getRegisteredUserJid() {
    return registeredUserJid;
  }
  
  public void setRegisteredUserJid(String registeredUserJid) {
    this.registeredUserJid = registeredUserJid;
  }
  
  public String getRegisteredUserPassword() {
    return registeredUserPassword;
  }
  
  public void setRegisteredUserPassword(String registeredUserPassword) {
    this.registeredUserPassword = registeredUserPassword;
  }
  
  @LoggedIn
  @Secure (Permission.SYSTEM_ADMINISTRATION)
  public void saveRegisteredUser() throws UnsupportedEncodingException, GeneralSecurityException {
    User user = userController.findUserById(getRegisteredUserId());
    
    UserChatCredentials userChatCredentials = chatCredentialsController.findUserChatCredentialsByUser(user);
    if (userChatCredentials != null) {
      chatCredentialsController.updateUserChatCredentialsUserJid(userChatCredentials, getRegisteredUserJid());
      chatCredentialsController.updateUserChatCredentialsPassword(userChatCredentials, getRegisteredUserPassword());
    } else {
      chatCredentialsController.createUserChatCredentials(user, getRegisteredUserJid(), getRegisteredUserPassword());
    }
  }
  
  private String generateUniqueXmppUserJid(User user) {
    String userEmail = getUserEmail(user);
    String userName = StringUtils.lowerCase(StringUtils.split(userEmail, '@')[0]);
    
    int i = 0;
    while (true) {
      String userJid = userName + (i > 0 ? i : "") + '@' + xmppDomain;
      if (!generatedUserJids.containsValue(userJid) && !existingUserJids.contains(userJid)) {
        return userJid;
      }
      
      i++;
    }
  }
  
  private String generateXmppPassword() {
    return RandomStringUtils.randomAlphabetic(7);
  }
	
	private List<User> users;
	private String xmppBoshService;
	private String xmppDomain;
	private List<String> existingUserJids;
	private Map<Long, String> generatedUserJids;
	private Map<Long, String> generatedPasswords;
	private Long registeredUserId;
	private String registeredUserJid;
	private String registeredUserPassword;
}

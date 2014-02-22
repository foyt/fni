package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLAction.PhaseId;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserRole;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "illusion-create-group", 
		pattern = "/illusion/creategroup", 
		viewId = "/illusion/creategroup.jsf"
  )
})
public class IllusionCreateGroupBackingBean {

  @Inject
  private SessionController sessionController;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private ChatCredentialsController chatCredentialsController;

  @Inject
  private IllusionGroupController illusionGroupController;

  @LoggedIn
// TODO: Security
	@URLAction
	public void load() throws IOException, GeneralSecurityException {
	  User user = sessionController.getLoggedUser();

	  userNickname = getUserNickname(user);
	  
	  xmppUserJid = chatCredentialsController.getUserJidByUser(user);
    xmppPassword = chatCredentialsController.getPasswordByUser(user);
    xmppBoshService = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOSH_SERVICE);
    xmppMucHost = systemSettingsController.getSetting(SystemSettingKey.CHAT_MUC_HOST);
    chatBotJid = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOT_JID);

    resolveNames();
	}
	
	@URLAction (phaseId = PhaseId.INVOKE_APPLICATION)
  public void applyValues() {
    resolveNames();
	}
	
	public String getName() {
    return name;
  }
	
	public void setName(String name) {
    this.name = name;
  }
	
	public String getDescription() {
    return description;
  }
	
	public void setDescription(String description) {
    this.description = description;
  }

	public String getXmppUserJid() {
    return xmppUserJid;
  }
	
	public String getXmppPassword() {
    return xmppPassword;
  }
	
	public String getXmppBoshService() {
    return xmppBoshService;
  }
  
  public String getXmppRoom() {
    return xmppRoom;
  }
  
  public String getChatBotJid() {
    return chatBotJid;
  }
  
	public String getUserNickname() {
    return userNickname;
  }
	
	public String getUrlName() {
    return urlName;
  }
	
	private void resolveNames() {
	  this.urlName = createUrlName(getName());
	  this.xmppRoom = urlName + '@' + xmppMucHost;
	}
	
	private String createUrlName(String name) {
    int maxLength = 20;
    int padding = 0;
    do {
      String urlName = RequestUtils.createUrlName(name, maxLength);
      if (padding > 0) {
        urlName = urlName.concat(StringUtils.repeat('_', padding));
      }
      
      IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(urlName);
      if (illusionGroup == null) {
        return urlName;
      }
      
      if (maxLength < name.length()) {
        maxLength++;
      } else {
        padding++;
      }
    } while (true);
  }
	
  @LoggedIn
// TODO: Security
  public void save() throws Exception {
    Date now = new Date();
    
    IllusionGroup group = illusionGroupController.createIllusionGroup(getUrlName(), getName(), getDescription(), getXmppRoom(), now);
    
    // Add game master
    User loggedUser = sessionController.getLoggedUser();
    illusionGroupController.createIllusionGroupUser(loggedUser, group, getUserNickname(loggedUser), IllusionGroupUserRole.GAMEMASTER);
    
    // Add bot 
    String botJid = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOT_JID);
    UserChatCredentials botChatCredentials = chatCredentialsController.findUserChatCredentialsByUserJid(botJid);
    if (botChatCredentials == null) {
      // TODO: Better error handling
      throw new Exception("Configuration error, could not find chatbot user");
    }
    
    illusionGroupController.createIllusionGroupUser(botChatCredentials.getUser(), group, getUserNickname(botChatCredentials.getUser()), IllusionGroupUserRole.BOT);
    
    String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
    
    FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
      .append(contextPath)
      .append("/illusion/group/")
      .append(group.getUrlName())
      .toString());
  }

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }
	
	private String name;
	private String description;
	private String xmppBoshService;
	private String xmppUserJid;
	private String xmppPassword;
	private String xmppMucHost;
  private String xmppRoom;
  private String chatBotJid;
	private String userNickname;
	private String urlName;
}

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
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.Phase;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.materials.IllusionGroupDocumentController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserRole;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.faces.FacesUtils;
import fi.foyt.fni.utils.servlet.RequestUtils;

@RequestScoped
@Stateful
@Named
@Join (path = "/illusion/creategroup", to = "/illusion/creategroup.jsf")
@LoggedIn
public class IllusionCreateGroupBackingBean {

  @Inject
  private SessionController sessionController;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private ChatCredentialsController chatCredentialsController;

  @Inject
  private IllusionGroupController illusionGroupController;

  @Inject
  private IllusionGroupDocumentController illusionGroupDocumentController;
  
	@RequestAction
	public void load() throws IOException, GeneralSecurityException {
    resolveNames();
	}
	
	@RequestAction
	@Deferred (after = Phase.UPDATE_MODEL_VALUES)
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
	
	public String getUrlName() {
    return urlName;
  }
	
	private void resolveNames() {
	  this.urlName = createUrlName(getName());
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
	
  public void save() throws Exception {
    Date now = new Date();
    String xmppRoom = urlName + '@' + systemSettingsController.getSetting(SystemSettingKey.CHAT_MUC_HOST);
    User loggedUser = sessionController.getLoggedUser();
    Language language = systemSettingsController.findLocaleByIso2(sessionController.getLocale().getLanguage());

    IllusionFolder illusionFolder = illusionGroupController.findUserIllusionFolder(loggedUser, true);
    IllusionGroupFolder illusionGroupFolder = illusionGroupController.createIllusionGroupFolder(loggedUser, illusionFolder, getUrlName(), getName());
    IllusionGroup group = illusionGroupController.createIllusionGroup(getUrlName(), getName(), getDescription(), xmppRoom, illusionGroupFolder, now);
    
    String indexDocumentTitle = FacesUtils.getLocalizedValue("illusion.createGroup.indexDocumentTitle");
    String indexDocumentContent = FacesUtils.getLocalizedValue("illusion.createGroup.indexDocumentContent");
    String previewDocumentTitle = FacesUtils.getLocalizedValue("illusion.createGroup.previewDocumentTitle");
    String previewDocumentContent = FacesUtils.getLocalizedValue("illusion.createGroup.previewDocumentContent");
    
    illusionGroupDocumentController.createIllusionGroupDocument(loggedUser, IllusionGroupDocumentType.INDEX, language, illusionGroupFolder, "index", indexDocumentTitle, indexDocumentContent, MaterialPublicity.PRIVATE);
    illusionGroupDocumentController.createIllusionGroupDocument(loggedUser, IllusionGroupDocumentType.PREVIEW, language, illusionGroupFolder, "preview", previewDocumentTitle, previewDocumentContent, MaterialPublicity.PRIVATE);
    
    // Add game master
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
	private String urlName;
}

package fi.foyt.fni.view.illusion;

import java.util.Currency;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.materials.IllusionEventDocumentController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
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
@Join (path = "/illusion/createevent", to = "/illusion/createevent.jsf")
@LoggedIn
public class IllusionCreateEventBackingBean {

  @Inject
  private SessionController sessionController;
  
  @Inject
  private SystemSettingsController systemSettingsController;
  
  @Inject
  private ChatCredentialsController chatCredentialsController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventDocumentController illusionEventDocumentController;
  
  @PostConstruct
  public void init() {
    signUpFee = null;
    signUpFeeCurrency = systemSettingsController.getDefaultCurrency().getCurrencyCode();
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
	
	public IllusionEventJoinMode getJoinMode() {
    return joinMode;
  }
	
	public void setJoinMode(IllusionEventJoinMode joinMode) {
    this.joinMode = joinMode;
  }
	
	public Double getSignUpFee() {
    return signUpFee;
  }
	
	public void setSignUpFee(Double signUpFee) {
    this.signUpFee = signUpFee;
  }
	
	public String getSignUpFeeCurrency() {
    return signUpFeeCurrency;
  }
	
	public void setSignUpFeeCurrency(String signUpFeeCurrency) {
    this.signUpFeeCurrency = signUpFeeCurrency;
  }
	
	private String createUrlName(String name) {
    int maxLength = 20;
    int padding = 0;
    do {
      String urlName = RequestUtils.createUrlName(name, maxLength);
      if (padding > 0) {
        urlName = urlName.concat(StringUtils.repeat('_', padding));
      }
      
      IllusionEvent illusionEvent = illusionEventController.findIllusionGroupByUrlName(urlName);
      if (illusionEvent == null) {
        return urlName;
      }
      
      if (maxLength < name.length()) {
        maxLength++;
      } else {
        padding++;
      }
    } while (true);
  }
	
  public String save() throws Exception {
    Date now = new Date();
    
    String urlName = createUrlName(getName());
    String xmppRoom = urlName + '@' + systemSettingsController.getSetting(SystemSettingKey.CHAT_MUC_HOST);
    User loggedUser = sessionController.getLoggedUser();
    Language language = systemSettingsController.findLocaleByIso2(sessionController.getLocale().getLanguage());
    Double signUpFee = getSignUpFee();
    Currency signUpFeeCurrency = null;
    
    if (signUpFee != null && signUpFee <= 0) {
      signUpFee = null;
    }
    
    if (signUpFee != null) {
      signUpFeeCurrency = Currency.getInstance(getSignUpFeeCurrency());
    }

    IllusionFolder illusionFolder = illusionEventController.findUserIllusionFolder(loggedUser, true);
    IllusionEventFolder illusionEventFolder = illusionEventController.createIllusionGroupFolder(loggedUser, illusionFolder, urlName, getName());
    IllusionEvent event = illusionEventController.createIllusionGroup(urlName, getName(), getDescription(), xmppRoom, illusionEventFolder, getJoinMode(), now, signUpFee, signUpFeeCurrency);
    
    String indexDocumentTitle = FacesUtils.getLocalizedValue("illusion.createEvent.indexDocumentTitle");
    String indexDocumentContent = FacesUtils.getLocalizedValue("illusion.createEvent.indexDocumentContent");
    String introDocumentTitle = FacesUtils.getLocalizedValue("illusion.createEvent.introDocumentTitle");
    String introDocumentContent = FacesUtils.getLocalizedValue("illusion.createEvent.introDocumentContent");
    
    illusionEventDocumentController.createIllusionGroupDocument(loggedUser, IllusionEventDocumentType.INDEX, language, illusionEventFolder, "index", indexDocumentTitle, indexDocumentContent, MaterialPublicity.PRIVATE);
    illusionEventDocumentController.createIllusionGroupDocument(loggedUser, IllusionEventDocumentType.INTRO, language, illusionEventFolder, "intro", introDocumentTitle, introDocumentContent, MaterialPublicity.PRIVATE);
    
    // Add game master
    illusionEventController.createIllusionGroupMember(loggedUser, event, getUserNickname(loggedUser), IllusionEventParticipantRole.GAMEMASTER);
    
    // Add bot 
    String botJid = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOT_JID);
    UserChatCredentials botChatCredentials = chatCredentialsController.findUserChatCredentialsByUserJid(botJid);
    if (botChatCredentials == null) {
      // TODO: Better error handling
      throw new Exception("Configuration error, could not find chatbot user");
    }
    
    illusionEventController.createIllusionGroupMember(botChatCredentials.getUser(), event, getUserNickname(botChatCredentials.getUser()), IllusionEventParticipantRole.BOT);
    
    return "/illusion/event.jsf?faces-redirect=true&urlName=" + event.getUrlName();
  }

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }
	
	private String name;
	private String description;
	private IllusionEventJoinMode joinMode;
	private Double signUpFee;
	private String signUpFeeCurrency;
}

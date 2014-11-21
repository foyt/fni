package fi.foyt.fni.view.illusion;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ocpsoft.rewrite.annotation.Join;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.materials.IllusionEventDocumentController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventType;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
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
@Join(path = "/illusion/createevent", to = "/illusion/createevent.jsf")
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

    List<IllusionEventType> eventTypes = illusionEventController.listTypes();
    typeSelectItems = new ArrayList<>(eventTypes.size());
    for (IllusionEventType eventType : eventTypes) {
      typeSelectItems.add(new SelectItem(eventType.getId(), eventType.getName()));
    }

    genres = illusionEventController.listGenres();
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

  public String getLocation() {
    return location;
  }
  
  public void setLocation(String location) {
    this.location = location;
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

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
  
  public Integer getAgeLimit() {
    return ageLimit;
  }

  public void setAgeLimit(Integer ageLimit) {
    this.ageLimit = ageLimit;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Boolean getBeginnerFriendly() {
    return beginnerFriendly;
  }

  public void setBeginnerFriendly(Boolean beginnerFriendly) {
    this.beginnerFriendly = beginnerFriendly;
  }

  public String getSignUpStartDate() {
    return signUpStartDate;
  }
  
  public void setSignUpStartDate(String signUpStartDate) {
    this.signUpStartDate = signUpStartDate;
  }
  
  public String getSignUpEndDate() {
    return signUpEndDate;
  }
  
  public void setSignUpEndDate(String signUpEndDate) {
    this.signUpEndDate = signUpEndDate;
  }
  
  public Long getTypeId() {
    return typeId;
  }
  
  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  public List<Genre> getGenres() {
    return genres;
  }
  
  public List<SelectItem> getTypeSelectItems() {
    return typeSelectItems;
  }
  
  public List<Long> getGenreIds() {
    return genreIds;
  }
  
  public void setGenreIds(List<Long> genreIds) {
    this.genreIds = genreIds;
  }
  
  private String createUrlName(String name) {
    int maxLength = 20;
    int padding = 0;
    do {
      String urlName = RequestUtils.createUrlName(name, maxLength);
      if (padding > 0) {
        urlName = urlName.concat(StringUtils.repeat('_', padding));
      }

      IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(urlName);
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
    
    if (StringUtils.isBlank(getName())) {
      FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("illusion.createEvent.nameRequired"));
      return null;
    }

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
    IllusionEventFolder illusionEventFolder = illusionEventController.createIllusionEventFolder(loggedUser, illusionFolder, urlName, getName());
    IllusionEventType type = illusionEventController.findTypeById(getTypeId());
    Date signUpStartDate = parseDate(getSignUpStartDate());
    Date signUpEndDate = parseDate(getSignUpEndDate());
    
    IllusionEvent event = illusionEventController.createIllusionEvent(urlName, getLocation(), getName(), getDescription(), xmppRoom, illusionEventFolder, getJoinMode(), now,
        signUpFee, signUpFeeCurrency, parseDate(getStartDate()), parseDate(getStartTime()), parseDate(getEndDate()), parseDate(getEndTime()),
        getAgeLimit(), getBeginnerFriendly(), getImageUrl(), type, signUpStartDate, signUpEndDate);

    String indexDocumentTitle = FacesUtils.getLocalizedValue("illusion.createEvent.indexDocumentTitle");
    String indexDocumentContent = FacesUtils.getLocalizedValue("illusion.createEvent.indexDocumentContent");

    illusionEventDocumentController.createIllusionEventDocument(loggedUser, IllusionEventDocumentType.INDEX, language, illusionEventFolder, "index",
        indexDocumentTitle, indexDocumentContent, MaterialPublicity.PRIVATE);

    // Add organizer
    illusionEventController.createIllusionEventParticipant(loggedUser, event, getUserNickname(loggedUser), IllusionEventParticipantRole.ORGANIZER);

    // Add bot
    String botJid = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOT_JID);
    UserChatCredentials botChatCredentials = chatCredentialsController.findUserChatCredentialsByUserJid(botJid);
    if (botChatCredentials != null) {
      illusionEventController.createIllusionEventParticipant(botChatCredentials.getUser(), event, getUserNickname(botChatCredentials.getUser()),
          IllusionEventParticipantRole.BOT);
    }
    
    List<Genre> genres = new ArrayList<>();
    
    for (Long genreId : genreIds) {
      Genre genre = illusionEventController.findGenreById(genreId);
      genres.add(genre);
    }
    
    illusionEventController.updateEventGenres(event, genres);

    return "/illusion/event.jsf?faces-redirect=true&urlName=" + event.getUrlName();
  }

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }

  private Date parseDate(String iso) {
    if (StringUtils.isBlank(iso)) {
      return null;
    }

    DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
    return parser.parseDateTime(iso).toDate();
  }

  private String name;
  private String description;
  private String location;
  private IllusionEventJoinMode joinMode;
  private Double signUpFee;
  private String signUpFeeCurrency;
  private String startDate;
  private String startTime;
  private String endDate;
  private String endTime;
  private Integer ageLimit;
  private String imageUrl;
  private Boolean beginnerFriendly;
  private String signUpStartDate;
  private String signUpEndDate;
  private List<Genre> genres;
  private Long typeId;
  private List<Long> genreIds;
  private List<SelectItem> typeSelectItems;
}

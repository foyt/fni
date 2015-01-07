package fi.foyt.fni.illusion;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.illusion.GenreDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventGenreDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantImageDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventTemplateDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventTypeDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventDocumentDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventFolderDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionFolderDAO;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGenre;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantImage;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventTemplate;
import fi.foyt.fni.persistence.model.illusion.IllusionEventType;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Dependent
@Stateless
public class IllusionEventController {
  
  private static final String ILLUSION_FOLDER_TITLE = "Illusion";
  
  @Inject
  private IllusionEventDAO illusionEventDAO;

  @Inject
  private IllusionEventGenreDAO illusionEventGenreDAO;

  @Inject
  private IllusionEventParticipantDAO illusionEventParticipantDAO;

  @Inject
  private IllusionEventParticipantImageDAO illusionEventParticipantImageDAO;
  
  @Inject
  private IllusionFolderDAO illusionFolderDAO;

  @Inject
  private IllusionEventFolderDAO illusionEventFolderDAO;

  @Inject
  private IllusionEventTypeDAO illusionEventTypeDAO;
  
  @Inject
  private GenreDAO genreDAO;
  
  @Inject
  private IllusionEventDocumentDAO illusionEventDocumentDAO;

  @Inject
  private MaterialController materialController;

  @Inject
  private IllusionEventTemplateDAO illusionEventTemplateDAO;

  @Inject
  private IllusionEventSettingDAO illusionEventSettingDAO;
 
  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private ChatCredentialsController chatCredentialsController;
  
  @Inject
  private Event<IllusionParticipantAddedEvent> illusionParticipantAddedEvent;

  @Inject
  private Event<IllusionParticipantRoleChangeEvent> roleChangeEvent;
  
  /* IllusionEvent */


  public IllusionEvent createIllusionEvent(User user, Locale locale, String location, String name, String description, IllusionEventJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency, Date startDate, Date startTime, Date endDate, Date endTime, Integer ageLimit, Boolean beginnerFriendly, String imageUrl, IllusionEventType type, Date signUpStartDate, Date signUpEndDate, List<Genre> genres) {
    Language language = systemSettingsController.findLocaleByIso2(locale.getLanguage());
    String urlName = createUrlName(name);
    String xmppRoom = urlName + '@' + systemSettingsController.getSetting(SystemSettingKey.CHAT_MUC_HOST);

    IllusionFolder illusionFolder = findUserIllusionFolder(user, true);
    IllusionEventFolder illusionEventFolder = createIllusionEventFolder(user, illusionFolder, urlName, name);
    IllusionEvent event = createIllusionEvent(urlName, location, name, description, xmppRoom, illusionEventFolder, joinMode, created, signUpFee, signUpFeeCurrency, startDate, startTime, endDate, endTime, ageLimit, beginnerFriendly, imageUrl, type, signUpStartDate, signUpEndDate);

    String indexDocumentTitle = ExternalLocales.getText(locale, "illusion.newEvent.indexDocumentTitle");
    String indexDocumentContent = ExternalLocales.getText(locale, "illusion.newEvent.indexDocumentContent");

    createIllusionEventDocument(user, IllusionEventDocumentType.INDEX, language, illusionEventFolder, "index",
        indexDocumentTitle, indexDocumentContent, MaterialPublicity.PRIVATE);

    // Add bot
    String botJid = systemSettingsController.getSetting(SystemSettingKey.CHAT_BOT_JID);
    UserChatCredentials botChatCredentials = chatCredentialsController.findUserChatCredentialsByUserJid(botJid);
    if (botChatCredentials != null) {
      createIllusionEventParticipant(botChatCredentials.getUser(), event, getUserNickname(botChatCredentials.getUser()), IllusionEventParticipantRole.BOT);
    }
    
    updateEventGenres(event, genres);
    
    return event;
  }

  private IllusionEvent createIllusionEvent(String urlName, String location, String name, String description, String xmppRoom, IllusionEventFolder folder, IllusionEventJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency, Date startDate, Date startTime, Date endDate, Date endTime, Integer ageLimit, Boolean beginnerFriendly, String imageUrl, IllusionEventType type, Date signUpStartDate, Date signUpEndDate) {
    return illusionEventDAO.create(urlName, name, location, description, xmppRoom, folder, joinMode, created, signUpFee, signUpFeeCurrency, startDate, startTime, endDate, endTime, null, ageLimit, beginnerFriendly, imageUrl, type, signUpStartDate, signUpEndDate);
  }

  public IllusionEvent findIllusionEventById(Long id) {
    return illusionEventDAO.findById(id);
  }

  public IllusionEvent findIllusionEventByUrlName(String urlName) {
    return illusionEventDAO.findByUrlName(urlName);
  }

  public List<IllusionEvent> listIllusionEvents() {
    return illusionEventDAO.listAll();
  }

  public List<IllusionEvent> listIllusionEventsByUserAndRole(User user, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listIllusionEventsByUserAndRole(user, role);
  }

  public List<IllusionEvent> listNextIllusionEvents(int maxResults) {
    Date now = new Date();
    return illusionEventDAO.listByStartDateGEOrEndDateGESortByStartDateAndStartTime(now, now, 0, maxResults);
  }

  public List<IllusionEvent> listPastIllusionEvents(int maxResults) {
    Date now = new Date();
    return illusionEventDAO.listByStartDateLTAndEndDateLTSortByEndDateEndTimeStartDateStartTime(now, now, 0, maxResults);
  }

  public List<IllusionEvent> listIllusionEventsWithDomain() {
    return illusionEventDAO.listByDomainNotNull();
  }

  public IllusionEvent updateIllusionEventName(IllusionEvent illusionEvent, String name) {
    return illusionEventDAO.updateName(illusionEvent, name);
  }

  public IllusionEvent updateIllusionEventUrlName(IllusionEvent illusionEvent, String urlName) {
    return illusionEventDAO.updateUrlName(illusionEvent, urlName);
  }

  public IllusionEvent updateIllusionEventDescription(IllusionEvent illusionEvent, String description) {
    return illusionEventDAO.updateDescription(illusionEvent, description);
  }

  public IllusionEvent updateIllusionEventJoinMode(IllusionEvent illusionEvent, IllusionEventJoinMode joinMode) {
    return illusionEventDAO.updateJoinMode(illusionEvent, joinMode);
  }

  public IllusionEvent updateIllusionEventStartDate(IllusionEvent illusionEvent, Date startDate) {
    return illusionEventDAO.updateStartDate(illusionEvent, startDate);
  }

  public IllusionEvent updateIllusionEventStartTime(IllusionEvent illusionEvent, Date startTime) {
    return illusionEventDAO.updateStartTime(illusionEvent, startTime);
  }

  public IllusionEvent updateIllusionEventEndDate(IllusionEvent illusionEvent, Date endDate) {
    return illusionEventDAO.updateEndDate(illusionEvent, endDate);
  }

  public IllusionEvent updateIllusionEventEndTime(IllusionEvent illusionEvent, Date endTime) {
    return illusionEventDAO.updateEndTime(illusionEvent, endTime);
  }

  public IllusionEvent updateIllusionEventLocation(IllusionEvent illusionEvent, String location) {
    return illusionEventDAO.updateLocation(illusionEvent, location);
  }
  
  public IllusionEvent updateEventOAuthClient(IllusionEvent illusionEvent, OAuthClient oAuthClient) {
    return illusionEventDAO.updateOAuthClient(illusionEvent, oAuthClient);
  }

  public IllusionEvent updateEventDomain(IllusionEvent illusionEvent, String domain) {
    return illusionEventDAO.updateDomain(illusionEvent, domain);
  }

  public IllusionEvent updateIllusionEventType(IllusionEvent illusionEvent, IllusionEventType type) {
    return illusionEventDAO.updateType(illusionEvent, type);
  }

  public IllusionEvent updateIllusionEventSignUpTimes(IllusionEvent illusionEvent, Date signUpStartDate, Date signUpEndDate) {
    return illusionEventDAO.updateSignUpEndDate(illusionEventDAO.updateSignUpStartDate(illusionEvent, signUpStartDate), signUpEndDate);
  }
  
  public IllusionEvent updateIllusionEventAgeLimit(IllusionEvent illusionEvent, Integer ageLimit) {
    return illusionEventDAO.updateAgeLimit(illusionEvent, ageLimit);
  }
  
  public IllusionEvent updateIllusionEventBeginnerFriendly(IllusionEvent illusionEvent, Boolean beginnerFriendly) {
    return illusionEventDAO.updateBeginnerFriendly(illusionEvent, beginnerFriendly);
  }
  
  public IllusionEvent updateIllusionEventImageUrl(IllusionEvent illusionEvent, String imageUrl) {
    return illusionEventDAO.updateImageUrl(illusionEvent, imageUrl);
  }

  public void deleteIllusionEvent(IllusionEvent event) {
    illusionEventDAO.delete(event);
  }
  
  public IllusionEvent updateEventGenres(IllusionEvent event, List<Genre> genres) {
    List<IllusionEventGenre> existingGenres = illusionEventGenreDAO.listByEvent(event);
    List<Genre> addGenres = new ArrayList<>(genres);
    
    Map<Long, IllusionEventGenre> existingGenreMap = new HashMap<>();
    for (IllusionEventGenre existingGender : existingGenres) {
      existingGenreMap.put(existingGender.getGenre().getId(), existingGender);
    }
    
    for (int i = addGenres.size() - 1; i >= 0; i--) {
      Genre addGenre = addGenres.get(i);
      
      if (existingGenreMap.containsKey(addGenre.getId())) {
        addGenres.remove(i);
      } 
      
      existingGenreMap.remove(addGenre.getId());
    }
    
    for (IllusionEventGenre removeGenre : existingGenreMap.values()) {
      illusionEventGenreDAO.delete(removeGenre);
    }
    
    for (Genre genre : addGenres) {
      illusionEventGenreDAO.create(event, genre);
    }
    
    return event;
  }
  
  /* IllusionEventParticipant */
  
  public IllusionEventParticipant createIllusionEventParticipant(User user, IllusionEvent event, String characterName, IllusionEventParticipantRole role) {
    IllusionEventParticipant member = illusionEventParticipantDAO.create(user, event, characterName, role);
    illusionParticipantAddedEvent.fire(new IllusionParticipantAddedEvent(member.getId()));
    
    return member;
  }
  
  public IllusionEventParticipant createIllusionEventParticipant(User user, IllusionEvent event, IllusionEventParticipantRole organizer) {
    return createIllusionEventParticipant(user, event, getUserNickname(user), organizer);
  } 

  public IllusionEventParticipant findIllusionEventParticipantById(Long id) {
    return illusionEventParticipantDAO.findById(id);
  }
  
  public IllusionEventParticipant findIllusionEventParticipantByEventAndUser(IllusionEvent event, User user) {
    return illusionEventParticipantDAO.findByEventAndUser(event, user);
  }
  
  public List<IllusionEventParticipant> listIllusionEventParticipantsByEvent(IllusionEvent event) {
    return illusionEventParticipantDAO.listByEvent(event);
  }
  
  public List<IllusionEventParticipant> listIllusionEventParticipantsByEventAndRole(IllusionEvent event, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listByEventAndRole(event, role);
  }

  public Long countIllusionEventParticipantsByEventAndRole(IllusionEvent event, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.countByEventAndRole(event, role);
  }
  
  public IllusionEventParticipant updateIllusionEventParticipantCharacterName(IllusionEventParticipant participant, String characterName) {
    return illusionEventParticipantDAO.updateCharacterName(participant, characterName);
  }

  public IllusionEventParticipant updateIllusionEventParticipantRole(IllusionEventParticipant participant, IllusionEventParticipantRole role) {
    IllusionEventParticipantRole oldRole = participant.getRole();
    illusionEventParticipantDAO.updateRole(participant, role);
    
    if (oldRole != role) {
      roleChangeEvent.fire(new IllusionParticipantRoleChangeEvent(participant.getId(), oldRole, role));
    }
    
    return participant;
  }

  private String createUrlName(String name) {
    int maxLength = 20;
    int padding = 0;
    do {
      String urlName = RequestUtils.createUrlName(name, maxLength);
      if (padding > 0) {
        urlName = urlName.concat(StringUtils.repeat('_', padding));
      }

      IllusionEvent illusionEvent = findIllusionEventByUrlName(urlName);
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

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }
  
  /* IllusionEventParticipantImage */

  public IllusionEventParticipantImage createIllusionEeventParticipantImage(IllusionEventParticipant participant, String contentType, byte[] data, Date modified) {
    return illusionEventParticipantImageDAO.create(participant, contentType, data, modified);
  }

  public IllusionEventParticipantImage findIllusionEventParticipantImageByParticipant(IllusionEventParticipant participant) {
    return illusionEventParticipantImageDAO.findByParticipant(participant);
  }
  
  public IllusionEventParticipantImage updateIllusionEventParticipantImage(IllusionEventParticipantImage image, String contentType, byte[] data, Date modified) {
    return illusionEventParticipantImageDAO.updateModified(illusionEventParticipantImageDAO.updateContentType(illusionEventParticipantImageDAO.updateData(image, data), contentType), modified);
  }

  /* IllusionFolder */
  
  public IllusionFolder findUserIllusionFolder(User user, boolean createMissing) {
    IllusionFolder illusionFolder = illusionFolderDAO.findByCreator(user);
    if (illusionFolder == null && createMissing) {
      String illusionUrlName = materialController.getUniqueMaterialUrlName(user, null, null,  ILLUSION_FOLDER_TITLE);
      illusionFolder = illusionFolderDAO.create(user, illusionUrlName, ILLUSION_FOLDER_TITLE, MaterialPublicity.PRIVATE);
    }
    
    return illusionFolder;
  }
  
  /* IllusionEventFolder */
  
  public IllusionEventFolder createIllusionEventFolder(User creator, IllusionFolder illusionFolder, String urlName, String title) {
    return illusionEventFolderDAO.create(creator, illusionFolder, urlName, title, MaterialPublicity.PRIVATE);
  }

  public IllusionEvent findIllusionEventByFolder(IllusionEventFolder folder) {
    return illusionEventDAO.findByFolder(folder);
  }

  public IllusionEvent findIllusionEventByDomain(String domain) {
    return illusionEventDAO.findByDomain(domain);
  }

  public IllusionEvent findIllusionEventByOAuthClient(OAuthClient oAuthClient) {
    return illusionEventDAO.findByOAuthClient(oAuthClient);
  }
  
  /* Types */
  
  public IllusionEventType findTypeById(Long id) {
    return illusionEventTypeDAO.findById(id);
  }

  public List<IllusionEventType> listTypes() {
    return illusionEventTypeDAO.listAll();
  }
  
  /* Genres */

  public Genre findGenreById(Long genreId) {
    return genreDAO.findById(genreId);
  }
  
  public List<Genre> listGenres() {
    return genreDAO.listAll();
  }

  public List<IllusionEventGenre> listIllusionEventGenres(IllusionEvent illusionEvent) {
    return illusionEventGenreDAO.listByEvent(illusionEvent);
  }
  
  /* Templates */
  
  public IllusionEventTemplate createEventTemplate(IllusionEvent event, String name, String data) {
    return illusionEventTemplateDAO.create(event, name, data, new Date());
  }
  
  public IllusionEventTemplate createEventTemplate(IllusionEvent event) {
    return createEventTemplate(event, UUID.randomUUID().toString(), null);
  }

  public IllusionEventTemplate findEventTemplateById(Long templateId) {
    return illusionEventTemplateDAO.findById(templateId);
  }
  
  public IllusionEventTemplate findEventTemplate(IllusionEvent event, String name) {
    return illusionEventTemplateDAO.findByEventAndName(event, name); 
  }

  public List<IllusionEventTemplate> listTemplates(IllusionEvent illusionEvent) {
    return illusionEventTemplateDAO.listByEvent(illusionEvent);
  }

  public IllusionEventTemplate updateEventTemplateName(IllusionEventTemplate template, String templateName) {
    return illusionEventTemplateDAO.updateName(illusionEventTemplateDAO.updateModified(template, new Date()), templateName);
  }

  public IllusionEventTemplate updateEventTemplateData(IllusionEventTemplate template, String templateData) {
    return illusionEventTemplateDAO.updateData(illusionEventTemplateDAO.updateModified(template, new Date()), templateData);
  }

  public void deleteEventTemplate(IllusionEventTemplate template) {
    illusionEventTemplateDAO.delete(template);
  }
  
  /* Settings */
  
  public synchronized String getSetting(IllusionEvent event, IllusionEventSettingKey key) {
    IllusionEventSetting eventSetting = illusionEventSettingDAO.findByEventAndKey(event, key);
    if (eventSetting != null) {
      return eventSetting.getValue();
    }
    
    return null;
  }
  
  public synchronized void setSetting(IllusionEvent event, IllusionEventSettingKey key, String value) {
    IllusionEventSetting eventSetting = illusionEventSettingDAO.findByEventAndKey(event, key);
    if (eventSetting != null) {
      illusionEventSettingDAO.updateValue(eventSetting, value);
    } else {
      illusionEventSettingDAO.create(event, key, value);
    }
  }

  /* IllusionEventDocument */
  
  public IllusionEventDocument findByFolderAndDocumentType(IllusionEventFolder folder, IllusionEventDocumentType documentType) {
    return illusionEventDocumentDAO.findByParentFolderAndDocumentType(folder, documentType);
  }

  public IllusionEventDocument createIllusionEventDocument(User creator, IllusionEventDocumentType documentType, Language language, IllusionEventFolder parentFolder, String urlName, String title, String data, MaterialPublicity publicity, Integer indexNumber) {
    return illusionEventDocumentDAO.create(creator, documentType, language, parentFolder, urlName, title, data, publicity, indexNumber);
  }
  
  public IllusionEventDocument createIllusionEventDocument(User creator, IllusionEventDocumentType documentType, Language language, IllusionEventFolder parentFolder, String urlName, String title, String data, MaterialPublicity publicity) {
    Integer indexNumber = illusionEventDocumentDAO.maxIndexNumberByParentFolder(parentFolder);
    if (indexNumber == null) {
      indexNumber = 0;
    } else {
      indexNumber++;
    }
    
    return createIllusionEventDocument(creator, documentType, language, parentFolder, urlName, title, data, publicity, indexNumber);
  } 
}

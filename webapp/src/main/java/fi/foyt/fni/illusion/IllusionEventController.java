package fi.foyt.fni.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.auth.OAuthController;
import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.larpkalenteri.AVIResolver;
import fi.foyt.fni.larpkalenteri.AVIResolver.AVIProperties;
import fi.foyt.fni.larpkalenteri.LarpKalenteriClient;
import fi.foyt.fni.larpkalenteri.LarpKalenteriEventMissingException;
import fi.foyt.fni.larpkalenteri.UnsupportedTypeException;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.illusion.GenreDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventGenreDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventGroupDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventMaterialParticipantSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantImageDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventRegistrationFormDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventRegistrationFormFieldAnswerDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventRegistrationFormFieldDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventTemplateDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventTypeDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventDocumentDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventFolderDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionFolderDAO;
import fi.foyt.fni.persistence.dao.users.UserGroupMemberDAO;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGenre;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantImage;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventPaymentMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormField;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormFieldAnswer;
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
import fi.foyt.fni.persistence.model.users.UserGroupMember;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Dependent
public class IllusionEventController {
  
  private static final String ILLUSION_FOLDER_TITLE = "Illusion";

  @Inject
  private Logger logger;

  @Inject
  private IllusionEventDAO illusionEventDAO;

  @Inject
  private IllusionEventGenreDAO illusionEventGenreDAO;

  @Inject
  private IllusionEventParticipantDAO illusionEventParticipantDAO;

  @Inject
  private IllusionEventParticipantSettingDAO illusionEventParticipantSettingDAO;

  @Inject
  private IllusionEventMaterialParticipantSettingDAO illusionEventMaterialParticipantSettingDAO;

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
  private IllusionEventGroupDAO illusionEventParticipantGroupDAO;

  @Inject
  private UserGroupMemberDAO userGroupMemberDAO;

  @Inject
  private IllusionEventRegistrationFormDAO illusionEventRegistrationFormDAO;

  @Inject
  private IllusionEventRegistrationFormFieldDAO illusionEventRegistrationFormFieldDAO;
  
  @Inject
  private IllusionEventRegistrationFormFieldAnswerDAO illusionEventRegistrationFormFieldAnswerDAO;
  
  @Inject
  private OAuthController oAuthController;

  @Inject
  private ForumController forumController;

  @Inject
  private UserController userController;

  @Inject
  private LarpKalenteriClient larpKalenteriClient;

  @Inject
  private Event<IllusionParticipantAddedEvent> illusionParticipantAddedEvent;

  @Inject
  private Event<IllusionParticipantRoleChangeEvent> roleChangeEvent;
  
  /* IllusionEvent */

  public IllusionEvent createIllusionEvent(User user, Locale locale, String location, String name, String description, IllusionEventJoinMode joinMode, Date created, Double signUpFee, String signUpFeeText, Currency signUpFeeCurrency, Date start, Date end, Integer ageLimit, Boolean beginnerFriendly, String imageUrl, IllusionEventType type, Date signUpStartDate, Date signUpEndDate, List<Genre> genres) {
    Language language = systemSettingsController.findLocaleByIso2(locale.getLanguage());
    String urlName = createUrlName(name);
    String xmppRoom = urlName + '@' + systemSettingsController.getSetting(SystemSettingKey.CHAT_MUC_HOST);

    IllusionFolder illusionFolder = findUserIllusionFolder(user, true);
    IllusionEventFolder illusionEventFolder = createIllusionEventFolder(user, illusionFolder, urlName, name);
    IllusionEvent event = createIllusionEvent(urlName, location, name, description, xmppRoom, illusionEventFolder, joinMode, created, signUpFee, signUpFeeText, signUpFeeCurrency, start, end, ageLimit, beginnerFriendly, imageUrl, type, signUpStartDate, signUpEndDate, IllusionEventPaymentMode.NONE);

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

  private IllusionEvent createIllusionEvent(String urlName, String location, String name, String description, String xmppRoom, IllusionEventFolder folder, IllusionEventJoinMode joinMode, Date created, Double signUpFee, String signUpFeeText, Currency signUpFeeCurrency, Date start, Date end, Integer ageLimit, Boolean beginnerFriendly, String imageUrl, IllusionEventType type, Date signUpStartDate, Date signUpEndDate, IllusionEventPaymentMode paymentMode) {
    Forum forum = forumController.findForumByUrlName("illusion");
    String systemUserEmail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    User systemUser = userController.findUserByEmail(systemUserEmail);
    ForumTopic forumTopic = forumController.createTopic(forum, name, systemUser);
    return illusionEventDAO.create(urlName, name, location, description, xmppRoom, folder, joinMode, created, signUpFee, signUpFeeText, signUpFeeCurrency, start, end, null, ageLimit, beginnerFriendly, imageUrl, type, signUpStartDate, signUpEndDate, Boolean.FALSE, forumTopic, paymentMode);
  }

  public IllusionEvent findIllusionEventById(Long id) {
    return illusionEventDAO.findById(id);
  }

  public IllusionEvent findIllusionEventByUrlName(String urlName) {
    return illusionEventDAO.findByUrlName(urlName);
  }

  public IllusionEvent findEventByForumTopic(ForumTopic topic) {
    return illusionEventDAO.findByForumTopic(topic);
  }

  public List<IllusionEvent> listIllusionEvents() {
    return illusionEventDAO.listAll();
  }
  
  public List<IllusionEvent> listPublishedEvents() {
    return illusionEventDAO.listByPublishedOrderByStartAndEnd(Boolean.TRUE);
  }
  
  public List<IllusionEvent> listPublishedEventsByUsersAndRole(List<User> users, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listIllusionEventsByUsersAndRole(users, role, Boolean.TRUE);
  }
  
  public List<IllusionEvent> listPublishedEventsByUserAndRole(User user, IllusionEventParticipantRole role) {
    return listPublishedEventsByUsersAndRole(Collections.singletonList(user), role);
  }

  public List<IllusionEvent> listNextIllusionEvents(int maxResults) {
    Date now = new Date();
    return illusionEventDAO.listByStartGEOrEndGEAndPublishedSortByStart(now, now, Boolean.TRUE, 0, maxResults);
  }

  public List<IllusionEvent> listPastIllusionEvents(int maxResults) {
    Date now = new Date();
    return illusionEventDAO.listByStartLTAndEndLTAndPublishedSortByEndAndStart(now, now, Boolean.TRUE, 0, maxResults);
  }

  public List<IllusionEvent> listPublishedEventsBetween(Date start, Date end, Boolean published) {
    return illusionEventDAO.listByStartLEAndGEAndPublishedSortByEndAndStart(end, start, Boolean.TRUE);
  }
  
  public List<IllusionEvent> listIllusionEventsWithDomain() {
    return illusionEventDAO.listByDomainNotNull();
  }

  public List<IllusionEvent> listEventsByUserOrganizedAndUnpublished(User user) {
    List<IllusionEvent> result = new ArrayList<>();
    
    for (IllusionEventParticipant organizer : illusionEventParticipantDAO.listByUserAndRole(user, IllusionEventParticipantRole.ORGANIZER)) {
      if (!organizer.getEvent().getPublished()) {
        result.add(organizer.getEvent());
      }
    }
    
    return result;
  }

  public IllusionEvent updateIllusionEventName(IllusionEvent illusionEvent, String name) {
    if (!illusionEvent.getName().equals(name)) {
      String urlName = createUrlName(name);
      illusionEventDAO.updateName(illusionEvent, name);
      illusionEventDAO.updateUrlName(illusionEvent, urlName);
    }
    
    return illusionEvent;
  }

  public IllusionEvent updateIllusionEventDescription(IllusionEvent illusionEvent, String description) {
    return illusionEventDAO.updateDescription(illusionEvent, description);
  }

  public IllusionEvent updateIllusionEventJoinMode(IllusionEvent illusionEvent, IllusionEventJoinMode joinMode) {
    return illusionEventDAO.updateJoinMode(illusionEvent, joinMode);
  }

  public IllusionEvent updateIllusionEventStart(IllusionEvent illusionEvent, Date start) {
    return illusionEventDAO.updateStart(illusionEvent, start);
  }

  public IllusionEvent updateIllusionEventEnd(IllusionEvent illusionEvent, Date end) {
    return illusionEventDAO.updateEnd(illusionEvent, end);
  }

  public IllusionEvent updateIllusionEventLocation(IllusionEvent illusionEvent, String location) {
    return illusionEventDAO.updateLocation(illusionEvent, location);
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
  
  public IllusionEvent publishEvent(IllusionEvent illusionEvent) {
    return illusionEventDAO.updatePublished(illusionEvent, Boolean.TRUE);
  }
  
  public IllusionEvent unpublishEvent(IllusionEvent illusionEvent) {
    return illusionEventDAO.updatePublished(illusionEvent, Boolean.FALSE);
  }
  
  public IllusionEvent updatePublished(IllusionEvent illusionEvent, Boolean published) {
    if (!illusionEvent.getPublished().equals(published)) {
      if (published) {
        return publishEvent(illusionEvent);
      } else {
        return unpublishEvent(illusionEvent);
      }
    }
    
    return illusionEvent;
  }
  
  public boolean isEventAllowedDomain(String domain) {
    // RegEx from
    // http://stackoverflow.com/questions/10306690/domain-name-validation-with-regex
    return domain.matches("^(([a-zA-Z]{1})|([a-zA-Z]{1}[a-zA-Z]{1})|([a-zA-Z]{1}[0-9]{1})|([0-9]{1}[a-zA-Z]{1})|([a-zA-Z0-9][a-zA-Z0-9-_]{1,61}[a-zA-Z0-9]))\\.([a-zA-Z]{2,6}|[a-zA-Z0-9-]{2,30}\\.[a-zA-Z]{2,3})$");
  }

  public IllusionEvent updateEventDomain(IllusionEvent illusionEvent, String domain) {
    if (StringUtils.isNotBlank(domain)) {
      if (illusionEvent.getOAuthClient() == null) {
        String clientId = UUID.randomUUID().toString();
        String clientSecret = UUID.randomUUID().toString();
  
        String redirectUrl = new StringBuilder(systemSettingsController.getHostUrl(domain, false, true)).append(
            "/login/?return=1&loginMethod=ILLUSION_INTERNAL").toString();
  
        OAuthClient oAuthClient = oAuthController.createUserClient(illusionEvent.getName(), clientId, clientSecret, redirectUrl);
        illusionEventDAO.updateOAuthClient(illusionEvent, oAuthClient);
      }
    }

    return illusionEventDAO.updateDomain(illusionEvent, StringUtils.isNotBlank(domain) ? domain : null);
  }

  public IllusionEvent updateEventSignUpFee(IllusionEvent illusionEvent, String signUpFeeText, Double signUpFee, Currency signUpFeeCurrency, IllusionEventPaymentMode paymentMode) {
    illusionEventDAO.updateSignUpFee(illusionEvent, signUpFee);
    illusionEventDAO.updateSignUpFeeText(illusionEvent, signUpFeeText);
    illusionEventDAO.updateSignUpFeeCurrency(illusionEvent, signUpFeeCurrency);
    illusionEventDAO.updatePaymentMode(illusionEvent, paymentMode);
    
    return illusionEvent;
  }
  
  public String getEventUrl(IllusionEvent event) {
    if (StringUtils.isNotBlank(event.getDomain())) {
      return systemSettingsController.getHostUrl(event.getDomain(), false, true);
    } else {
      return systemSettingsController.getSiteUrl(false, true) + "/illusion/event/" + event.getUrlName();
    }
  }
  
  public void deleteIllusionEvent(IllusionEvent event) {
    for (IllusionEventParticipant participant : listIllusionEventParticipantsByEvent(event)) {
      deleteParticipant(participant);
    }
    
    for (IllusionEventTemplate template : listTemplates(event)) {
      deleteEventTemplate(template);
    }
    
    for (IllusionEventGroup group : listGroups(event)) {
      deleteGroup(group);
    }
    
    for (IllusionEventSetting setting : illusionEventSettingDAO.listByEvent(event)) {
      illusionEventSettingDAO.delete(setting);
    }
    
    for (IllusionEventGenre genre : listIllusionEventGenres(event)) {
      illusionEventGenreDAO.delete(genre);
    }
    
    if (event.getForumTopic() != null) {
      forumController.deleteTopic(event.getForumTopic());
    }
    
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
  
  public IllusionEventParticipant createIllusionEventParticipant(User user, IllusionEvent event, String displayName, IllusionEventParticipantRole role) {
    IllusionEventParticipant participant = illusionEventParticipantDAO.create(user, event, displayName, role, new Date());
    illusionParticipantAddedEvent.fire(new IllusionParticipantAddedEvent(participant.getId()));
    
    return participant;
  }
  
  public IllusionEventParticipant createIllusionEventParticipant(User user, IllusionEvent event, IllusionEventParticipantRole role) {
    return createIllusionEventParticipant(user, event, getUserNickname(user), role);
  } 

  public IllusionEventParticipant findIllusionEventParticipantById(Long id) {
    return illusionEventParticipantDAO.findById(id);
  }
  
  public IllusionEventParticipant findIllusionEventParticipantByEventAndUser(IllusionEvent event, User user) {
    return illusionEventParticipantDAO.findByEventAndUser(event, user);
  }

  public IllusionEventParticipant findParticipantByEventAndAccessCode(IllusionEvent event, String accessCode) {
    return illusionEventParticipantDAO.findByEventAndAccessCode(event, accessCode);
  }

  public List<IllusionEventParticipant> listIllusionEventParticipantsByEvent(IllusionEvent event) {
    return illusionEventParticipantDAO.listByEvent(event);
  }
  
  public List<IllusionEventParticipant> listIllusionEventParticipantsByEventAndRole(IllusionEvent event, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listByEventAndRoles(event, Collections.singletonList(role));
  }
  
  public List<IllusionEventParticipant> listIllusionEventParticipantsByEventAndRoles(IllusionEvent event, IllusionEventParticipantRole... roles) {
    return illusionEventParticipantDAO.listByEventAndRoles(event, Arrays.asList(roles));
  }

  public Long countIllusionEventParticipantsByEventAndRole(IllusionEvent event, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.countByEventAndRole(event, role);
  }
  
  public IllusionEventParticipant updateIllusionEventParticipantDisplayName(IllusionEventParticipant participant, String displayName) {
    return illusionEventParticipantDAO.updateDisplayName(participant, displayName);
  }

  public IllusionEventParticipant updateIllusionEventParticipantRole(IllusionEventParticipant participant, IllusionEventParticipantRole role) {
    IllusionEventParticipantRole oldRole = participant.getRole();
    illusionEventParticipantDAO.updateRole(participant, role);
    
    if (oldRole != role) {
      roleChangeEvent.fire(new IllusionParticipantRoleChangeEvent(participant.getId(), oldRole, role));
    }
    
    return participant;
  }

  public IllusionEventParticipant updateIllusionEventParticipantAccessCode(IllusionEventParticipant participant, String accessCode) {
    return illusionEventParticipantDAO.updateAccessCode(participant, accessCode);
  }

  public boolean isOneInRole(IllusionEvent event, List<User> users, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.countByEventAndRoleAndUsers(event, role, users) > 0;
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

  public void deleteParticipant(IllusionEventParticipant participant) {
    for (IllusionEventParticipantSetting setting : illusionEventParticipantSettingDAO.listByParticipant(participant)) {
      illusionEventParticipantSettingDAO.delete(setting);
    }

    IllusionEventParticipantImage participantImage = findIllusionEventParticipantImageByParticipant(participant);
    if (participantImage != null) {
      illusionEventParticipantImageDAO.delete(participantImage);
    }
    
    for (IllusionEventMaterialParticipantSetting materialParticipantSetting : illusionEventMaterialParticipantSettingDAO.listByParticipant(participant)) {
      illusionEventMaterialParticipantSettingDAO.delete(materialParticipantSetting);
    }
    
    illusionEventParticipantDAO.delete(participant);
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
  
  public String getSetting(IllusionEvent event, IllusionEventSettingKey key) {
    IllusionEventSetting eventSetting = illusionEventSettingDAO.findByEventAndKey(event, key);
    if (eventSetting != null) {
      return eventSetting.getValue();
    }
    
    return null;
  }
  
  public void setSetting(IllusionEvent event, IllusionEventSettingKey key, String value) {
    IllusionEventSetting eventSetting = illusionEventSettingDAO.findByEventAndKey(event, key);
    if (eventSetting != null) {
      if (value == null) {
        illusionEventSettingDAO.delete(eventSetting);
      } else {
        illusionEventSettingDAO.updateValue(eventSetting, value);
      }
    } else {
      if (value != null) {
        illusionEventSettingDAO.create(event, key, value);
      }
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

  /* Group */

  public IllusionEventGroup findGroupById(Long id) {
    return illusionEventParticipantGroupDAO.findById(id);
  }
  
  public IllusionEventGroup createGroup(IllusionEvent event, String name, User creator) {
    return illusionEventParticipantGroupDAO.create(event, name, creator);
  }
  
  public List<IllusionEventGroup> listGroups(IllusionEvent event) {
    return illusionEventParticipantGroupDAO.listByEvent(event);
  }
  
  public IllusionEventGroup updateGroupName(IllusionEventGroup group, String name) {
    return illusionEventParticipantGroupDAO.updateName(group, name);
  }
  
  public void deleteGroup(IllusionEventGroup group) {
    for (UserGroupMember member : listGroupMembers(group)) {
      deleteGroupMember(member); 
    }

    illusionEventParticipantGroupDAO.delete(group);
  }
  
  /* Member */
  
  public UserGroupMember createGroupMember(IllusionEventGroup group, User user) {
    return userGroupMemberDAO.create(group, user);
  }
  
  public List<UserGroupMember> listGroupMembers(IllusionEventGroup group) {
    return userGroupMemberDAO.listByGroup(group); 
  }
  
  public void deleteGroupMember(UserGroupMember member) {
    userGroupMemberDAO.delete(member);
  }

  public UserGroupMember findGroupMemberByGroupAndUser(IllusionEventGroup group, User user) {
    return userGroupMemberDAO.findByGroupAndUser(group, user);
  }
  
  /* Larp-kalenteri */

  public void updateLarpKalenteriEvent(IllusionEvent illusionEvent, Double locationLat, Double locationLon) throws IOException, UnsupportedTypeException, LarpKalenteriEventMissingException {
    Long larpKalenteriId = NumberUtils.createLong(getSetting(illusionEvent, IllusionEventSettingKey.LARP_KALENTERI_ID));
    List<IllusionEventParticipant> organizers = listIllusionEventParticipantsByEventAndRole(illusionEvent, IllusionEventParticipantRole.ORGANIZER);
    if (organizers.isEmpty()) {
      logger.warning(String.format("Event #%d does not have an organizer", illusionEvent.getId()));
    } else {
      String larpKalenteriType = larpKalenteriClient.translateType(illusionEvent.getType());
      List<IllusionEventGenre> eventGenres = listIllusionEventGenres(illusionEvent);
      List<Genre> genres = new ArrayList<>(eventGenres.size());
      for (IllusionEventGenre eventGenre : eventGenres) {
        genres.add(eventGenre.getGenre());
      }
      
      if (StringUtils.isBlank(larpKalenteriType)) {
        throw new UnsupportedTypeException();
      }
      
      if (larpKalenteriId == null) {
        createLarpKalenteriEvent(illusionEvent, larpKalenteriType, genres, organizers.get(0).getUser(), locationLat, locationLon);
      } else {
        updateLarpKalenteriEvent(illusionEvent, larpKalenteriId, larpKalenteriType, genres, organizers.get(0).getUser(), locationLat, locationLon);
      }
    }
  }
  
  private fi.foyt.fni.larpkalenteri.Event createLarpKalenteriEvent(IllusionEvent event, String larpKalenteriType, List<Genre> genres, User organizer, Double locationLat, Double locationLon) throws IOException {
    String password = RandomStringUtils.randomAlphabetic(5);
    String organizerName = userController.getUserDisplayName(organizer);
    String organizerEmail = userController.getUserPrimaryEmail(organizer);
    AVIProperties aviProperties = locationLat != null && locationLon != null ? new AVIResolver().query(locationLat, locationLon) : null;
    Long locationDropDown = larpKalenteriClient.translateAVI(aviProperties);
    
    fi.foyt.fni.larpkalenteri.Event larpKalenteriEvent = larpKalenteriClient.createEvent(
        event.getName(), 
        larpKalenteriType, 
        event.getStart(), 
        event.getEnd(), 
        null, 
        event.getSignUpStartDate(), 
        event.getSignUpEndDate(), 
        locationDropDown, 
        event.getLocation(), 
        event.getImageUrl(), 
        larpKalenteriClient.translateGenres(genres),
        event.getSignUpFeeText(), 
        event.getAgeLimit(), 
        event.getBeginnerFriendly(), 
        null, 
        event.getDescription(), 
        organizerName, 
        organizerEmail, 
        getEventUrl(event), 
        null, 
        event.getPublished() ? fi.foyt.fni.larpkalenteri.Event.Status.ACTIVE : fi.foyt.fni.larpkalenteri.Event.Status.PENDING, 
        password, 
        false, 
        event.getJoinMode() == IllusionEventJoinMode.INVITE_ONLY, 
        false, 
        event.getId());
    
    setSetting(event, IllusionEventSettingKey.LARP_KALENTERI_ID, String.valueOf(larpKalenteriEvent.getId()));
    
    return larpKalenteriEvent;
  }
  
  private fi.foyt.fni.larpkalenteri.Event updateLarpKalenteriEvent(IllusionEvent event, Long larpKalenteriId, String larpKalenteriType, List<Genre> genres, User organizer, Double locationLat, Double locationLon) throws IOException, LarpKalenteriEventMissingException {
    fi.foyt.fni.larpkalenteri.Event larpKalenteriEvent = larpKalenteriClient.findEvent(larpKalenteriId);
    if (larpKalenteriEvent == null) {
      throw new LarpKalenteriEventMissingException();
    } else {
      String organizerName = userController.getUserDisplayName(organizer);
      String organizerEmail = userController.getUserPrimaryEmail(organizer);
      Long locationDropDown = larpKalenteriEvent.getLocationDropDown();
      
      if (locationLat != null && locationLon != null) {
        if (!StringUtils.equals(larpKalenteriEvent.getLocation(), event.getLocation())) {
          AVIProperties aviProperties = new AVIResolver().query(locationLat, locationLon);
          locationDropDown = larpKalenteriClient.translateAVI(aviProperties);
        }
      }
      
      larpKalenteriEvent = larpKalenteriClient.updateEvent(
          larpKalenteriId,
          event.getName(), 
          larpKalenteriType, 
          event.getStart(), 
          event.getEnd(), 
          larpKalenteriEvent.getTextDate(), 
          event.getSignUpStartDate(), 
          event.getSignUpEndDate(), 
          locationDropDown,
          event.getLocation(), 
          event.getImageUrl(), 
          larpKalenteriClient.translateGenres(genres),
          event.getSignUpFeeText(), 
          event.getAgeLimit(), 
          event.getBeginnerFriendly(), 
          larpKalenteriEvent.getStoryDescription(), 
          event.getDescription(), 
          organizerName, 
          organizerEmail, 
          getEventUrl(event), 
          larpKalenteriEvent.getLink2(), 
          event.getPublished() ? fi.foyt.fni.larpkalenteri.Event.Status.ACTIVE : fi.foyt.fni.larpkalenteri.Event.Status.PENDING, 
          larpKalenteriEvent.getPassword(), 
          larpKalenteriEvent.getEventFull(), 
          event.getJoinMode() == IllusionEventJoinMode.INVITE_ONLY, 
          larpKalenteriEvent.getLanguageFree(), 
          event.getId());
      
      setSetting(event, IllusionEventSettingKey.LARP_KALENTERI_ID, String.valueOf(larpKalenteriEvent.getId()));
      
      return larpKalenteriEvent;
    }
  }
  
  /* Registration */
  
  public IllusionEventRegistrationForm findEventRegistrationForm(IllusionEvent event) {
    List<IllusionEventRegistrationForm> forms = illusionEventRegistrationFormDAO.listByEvent(event);
    if (forms.isEmpty()) {
      return null;
    } 
    
    if (forms.size() > 1) {
      logger.severe(String.format("Event %d has multiple registration forms", event.getId()));
    }
    
    return forms.get(0);
  }
  
  public IllusionEventRegistrationForm createEventRegistrationForm(IllusionEvent event, String formData) {
    List<IllusionEventRegistrationForm> forms = illusionEventRegistrationFormDAO.listByEvent(event);
    if (forms.isEmpty()) {
      return illusionEventRegistrationFormDAO.create(event, formData);
    } else {
      logger.severe(String.format("Event %d already has a registration form", event.getId()));
      return null;
    }
  }

  public IllusionEventRegistrationForm updateEventRegistrationForm(IllusionEventRegistrationForm form, String formData) {
    return illusionEventRegistrationFormDAO.updateData(form, formData);
  }

  public void saveRegistrationFormAnswers(IllusionEventRegistrationForm form, IllusionEventParticipant participant, Map<String, String> answers) {
    for (String fieldName : answers.keySet()) {
      String value = answers.get(fieldName);
      
      IllusionEventRegistrationFormField field = illusionEventRegistrationFormFieldDAO.findByFormAndName(form, fieldName);
      if (field == null) {
        field = illusionEventRegistrationFormFieldDAO.create(form, fieldName);
      }
      
      IllusionEventRegistrationFormFieldAnswer answer = illusionEventRegistrationFormFieldAnswerDAO.findByFieldAndParticipant(field, participant);
      if (answer == null) {
        illusionEventRegistrationFormFieldAnswerDAO.create(field, participant, value);
      } else {
        illusionEventRegistrationFormFieldAnswerDAO.updateValue(answer, value);
      }
    
    }
  }
  
  public Map<String, String> loadRegistrationFormAnswers(IllusionEventRegistrationForm form, IllusionEventParticipant participant) {
    Map<String, String> answers = new HashMap<>();
    
    List<IllusionEventRegistrationFormField> fields = illusionEventRegistrationFormFieldDAO.listByForm(form);
    for (IllusionEventRegistrationFormField field : fields) {
      IllusionEventRegistrationFormFieldAnswer answer = illusionEventRegistrationFormFieldAnswerDAO.findByFieldAndParticipant(field, participant);
      answers.put(field.getName(), answer != null ? answer.getValue() : "");
    }
    
    return answers;
  }
  
}

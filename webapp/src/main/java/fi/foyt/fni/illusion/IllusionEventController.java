package fi.foyt.fni.illusion;

import java.io.IOException;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantImageDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventParticipantSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionEventSettingDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionFolderDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionEventFolderDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantImage;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class IllusionEventController {
  
  private static final String ILLUSION_FOLDER_TITLE = "Illusion";
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionEventDAO illusionEventDAO;

  @Inject
  private IllusionEventParticipantDAO illusionEventParticipantDAO;

  @Inject
  private IllusionEventParticipantImageDAO illusionEventParticipantImageDAO;
  
  @Inject
  private IllusionEventSettingDAO illusionEventSettingDAO;

  @Inject
  private IllusionEventParticipantSettingDAO illusionEventParticipantSettingDAO;

  @Inject
  private IllusionFolderDAO illusionFolderDAO;

  @Inject
  private IllusionEventFolderDAO illusionEventFolderDAO;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private Event<IllusionParticipantAddedEvent> illusionParticipantAddedEvent;

  @Inject
  private Event<IllusionParticipantRoleChangeEvent> roleChangeEvent;
  
  /* IllusionEvent */

  public IllusionEvent createIllusionEvent(String urlName, String name, String description, String xmppRoom, IllusionEventFolder folder, IllusionEventJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency) {
    return illusionEventDAO.create(urlName, name, description, xmppRoom, folder, joinMode, created, signUpFee, signUpFeeCurrency);
  }

  public IllusionEvent findIllusionEventById(Long id) {
    return illusionEventDAO.findById(id);
  }

  public IllusionEvent findIllusionEventByUrlName(String urlName) {
    return illusionEventDAO.findByUrlName(urlName);
  }

  public List<IllusionEvent> listIllusionEventsByUserAndRole(User user, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listIllusionEventsByUserAndRole(user, role);
  }

  /* IllusionEventParticipant */
  
  public IllusionEventParticipant createIllusionEventParticipant(User user, IllusionEvent group, String characterName, IllusionEventParticipantRole role) {
    IllusionEventParticipant member = illusionEventParticipantDAO.create(user, group, characterName, role);
    illusionParticipantAddedEvent.fire(new IllusionParticipantAddedEvent(member.getId()));
    
    return member;
  }

  public IllusionEventParticipant findIllusionEventParticipantById(Long memberId) {
    return illusionEventParticipantDAO.findById(memberId);
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

  /* IllusionEventParticipantImage */

  public IllusionEventParticipantImage createIllusionEeventParticipantImage(IllusionEventParticipant participant, String contentType, byte[] data, Date modified) {
    return illusionEventParticipantImageDAO.create(participant, contentType, data, modified);
  }

  public IllusionEventParticipantImage findIllusionEventParticipantImageByParticipant(IllusionEventParticipant participant) {
    return illusionEventParticipantImageDAO.findByMember(participant);
  }
  
  public IllusionEventParticipantImage updateIllusionEventParticipantImage(IllusionEventParticipantImage image, String contentType, byte[] data, Date modified) {
    return illusionEventParticipantImageDAO.updateModified(illusionEventParticipantImageDAO.updateContentType(illusionEventParticipantImageDAO.updateData(image, data), contentType), modified);
  }
  
  /* Settings */
  
  public String getIllusionEventSettingValue(IllusionEventParticipant participant, IllusionEventSettingKey key) {
    IllusionEventParticipantSetting userSetting = illusionEventParticipantSettingDAO.findByParticipantAndKey(participant, key);
    if ((userSetting != null) && StringUtils.isNotBlank(userSetting.getValue())) {
      return userSetting.getValue();
    }
    
    IllusionEventSetting eventSetting = illusionEventSettingDAO.findByEventAndKey(participant.getEvent(), key);
    if (eventSetting != null) {
      return eventSetting.getValue();
    }
    
    return null;
  }
  
  public Object getIllusionEventParticipantSetting(IllusionEventParticipant participant, IllusionEventSettingKey key) {
    switch (key) {
      case DICE:
        return getIllusionEventDiceSetting(participant);
    }
    
    return null;
  }
  
  private <T> T getIllusionEventSetting(IllusionEventParticipant participant, IllusionEventSettingKey key, Class<T> clazz) {
    String value = getIllusionEventSettingValue(participant, key);
    if (StringUtils.isNotBlank(value)) {
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        return objectMapper.readValue(value, clazz);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not parse IllusionEventSetting " + key + " from user: " + participant.getId());
      }
    }
    
    return null;
  }
  
  public List<String> getIllusionEventDiceSetting(IllusionEventParticipant participant) {
    @SuppressWarnings("unchecked")
    List<String> result = getIllusionEventSetting(participant, IllusionEventSettingKey.DICE, List.class);
    if (result == null) {
      result = Collections.emptyList();
    }
    
    return result;
  }

  public IllusionEventParticipantSetting setIllusionEventSettingValue(IllusionEventParticipant participant, IllusionEventSettingKey key, String value) {
    IllusionEventParticipantSetting setting = illusionEventParticipantSettingDAO.findByParticipantAndKey(participant, key);
    if (setting == null) {
      return illusionEventParticipantSettingDAO.create(participant, key, value);
    } else {
      return illusionEventParticipantSettingDAO.updateValue(setting, value);
    }
  }

  public Map<IllusionEventSettingKey, Object> getIllusionEventParticipantSettings(IllusionEventParticipant participant) {
    Map<IllusionEventSettingKey, Object> result = new HashMap<>();
    
    for (IllusionEventSettingKey key : IllusionEventSettingKey.values()) {
      switch (key) {
        case DICE:
          result.put(key, getIllusionEventParticipantSetting(participant, key));
        break;
      }
    }
    
    return result;
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

}

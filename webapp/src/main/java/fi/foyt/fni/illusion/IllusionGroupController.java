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
import fi.foyt.fni.persistence.dao.materials.IllusionGroupFolderDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantImage;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class IllusionGroupController {
  
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
  private IllusionGroupFolderDAO illusionGroupFolderDAO;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private Event<IllusionParticipantAddedEvent> illusionParticipantAddedEvent;

  @Inject
  private Event<IllusionParticipantRoleChangeEvent> roleChangeEvent;
  
  /* IllusionEvent */

  public IllusionEvent createIllusionGroup(String urlName, String name, String description, String xmppRoom, IllusionGroupFolder folder, IllusionEventJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency) {
    return illusionEventDAO.create(urlName, name, description, xmppRoom, folder, joinMode, created, signUpFee, signUpFeeCurrency);
  }

  public IllusionEvent findIllusionGroupById(Long id) {
    return illusionEventDAO.findById(id);
  }

  public IllusionEvent findIllusionGroupByUrlName(String urlName) {
    return illusionEventDAO.findByUrlName(urlName);
  }

  public List<IllusionEvent> listIllusionGroupsByUserAndRole(User user, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listIllusionGroupsByUserAndRole(user, role);
  }

  /* IllusionEventParticipant */
  
  public IllusionEventParticipant createIllusionGroupMember(User user, IllusionEvent group, String characterName, IllusionEventParticipantRole role) {
    IllusionEventParticipant member = illusionEventParticipantDAO.create(user, group, characterName, role);
    illusionParticipantAddedEvent.fire(new IllusionParticipantAddedEvent(member.getId()));
    
    return member;
  }

  public IllusionEventParticipant findIllusionGroupMemberById(Long memberId) {
    return illusionEventParticipantDAO.findById(memberId);
  }
  
  public IllusionEventParticipant findIllusionGroupMemberByUserAndGroup(IllusionEvent group, User user) {
    return illusionEventParticipantDAO.findByGroupAndUser(group, user);
  }
  
  public List<IllusionEventParticipant> listIllusionGroupMembersByGroup(IllusionEvent group) {
    return illusionEventParticipantDAO.listByGroup(group);
  }
  
  public List<IllusionEventParticipant> listIllusionGroupMembersByGroupAndRole(IllusionEvent group, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.listByGroupAndRole(group, role);
  }

  public Long countIllusionGroupMembersByGroupAndRole(IllusionEvent group, IllusionEventParticipantRole role) {
    return illusionEventParticipantDAO.countByGroupAndRole(group, role);
  }
  
  public IllusionEventParticipant updateIllusionGroupMemberCharacterName(IllusionEventParticipant member, String characterName) {
    return illusionEventParticipantDAO.updateCharacterName(member, characterName);
  }

  public IllusionEventParticipant updateIllusionGroupMemberRole(IllusionEventParticipant member, IllusionEventParticipantRole role) {
    IllusionEventParticipantRole oldRole = member.getRole();
    IllusionEventParticipant groupMember = illusionEventParticipantDAO.updateRole(member, role);
    if (oldRole != role) {
      roleChangeEvent.fire(new IllusionParticipantRoleChangeEvent(member.getId(), oldRole, role));
    }
    
    return groupMember;
  }

  /* IllusionEventParticipantImage */

  public IllusionEventParticipantImage createIllusionGroupMemberImage(IllusionEventParticipant member, String contentType, byte[] data, Date modified) {
    return illusionEventParticipantImageDAO.create(member, contentType, data, modified);
  }

  public IllusionEventParticipantImage findIllusionGroupMemberImageByMember(IllusionEventParticipant member) {
    return illusionEventParticipantImageDAO.findByMember(member);
  }
  
  public IllusionEventParticipantImage updateIllusionGroupMemberImage(IllusionEventParticipantImage image, String contentType, byte[] data, Date modified) {
    return illusionEventParticipantImageDAO.updateModified(illusionEventParticipantImageDAO.updateContentType(illusionEventParticipantImageDAO.updateData(image, data), contentType), modified);
  }
  
  /* Settings */
  
  public String getIllusionGroupSettingValue(IllusionEventParticipant member, IllusionEventSettingKey key) {
    IllusionEventParticipantSetting userSetting = illusionEventParticipantSettingDAO.findByMemberAndKey(member, key);
    if ((userSetting != null) && StringUtils.isNotBlank(userSetting.getValue())) {
      return userSetting.getValue();
    }
    
    IllusionEventSetting groupSetting = illusionEventSettingDAO.findByUserAndKey(member.getGroup(), key);
    if (groupSetting != null) {
      return groupSetting.getValue();
    }
    
    return null;
  }
  
  public Object getIllusionGroupUserSetting(IllusionEventParticipant participant, IllusionEventSettingKey key) {
    switch (key) {
      case DICE:
        return getIllusionGroupDiceSetting(participant);
    }
    
    return null;
  }
  
  private <T> T getIllusionGroupSetting(IllusionEventParticipant user, IllusionEventSettingKey key, Class<T> clazz) {
    String value = getIllusionGroupSettingValue(user, key);
    if (StringUtils.isNotBlank(value)) {
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        return objectMapper.readValue(value, clazz);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not parse IllusionEventSetting " + key + " from user: " + user.getId());
      }
    }
    
    return null;
  }
  
  public List<String> getIllusionGroupDiceSetting(IllusionEventParticipant user) {
    @SuppressWarnings("unchecked")
    List<String> result = getIllusionGroupSetting(user, IllusionEventSettingKey.DICE, List.class);
    if (result == null) {
      result = Collections.emptyList();
    }
    
    return result;
  }

  public IllusionEventParticipantSetting setIllusionGroupSettingValue(IllusionEventParticipant member, IllusionEventSettingKey key, String value) {
    IllusionEventParticipantSetting setting = illusionEventParticipantSettingDAO.findByMemberAndKey(member, key);
    if (setting == null) {
      return illusionEventParticipantSettingDAO.create(member, key, value);
    } else {
      return illusionEventParticipantSettingDAO.updateValue(setting, value);
    }
  }

  public Map<IllusionEventSettingKey, Object> getIllusionGroupUserSettings(IllusionEventParticipant participant) {
    Map<IllusionEventSettingKey, Object> result = new HashMap<>();
    
    for (IllusionEventSettingKey key : IllusionEventSettingKey.values()) {
      switch (key) {
        case DICE:
          result.put(key, getIllusionGroupUserSetting(participant, key));
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
  
  /* IllusionGroupFolder */
  
  public IllusionGroupFolder createIllusionGroupFolder(User creator, IllusionFolder illusionFolder, String urlName, String title) {
    return illusionGroupFolderDAO.create(creator, illusionFolder, urlName, title, MaterialPublicity.PRIVATE);
  }

}

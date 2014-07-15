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
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupMemberDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupMemberImageDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupMemberSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupSettingDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionFolderDAO;
import fi.foyt.fni.persistence.dao.materials.IllusionGroupFolderDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupJoinMode;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberImage;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSettingKey;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.faces.FacesUtils;

@Dependent
@Stateless
public class IllusionGroupController {
  
  private static final String ILLUSION_FOLDER_TITLE = "Illusion";
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupDAO illusionGroupDAO;

  @Inject
  private IllusionGroupMemberDAO illusionGroupMemberDAO;

  @Inject
  private IllusionGroupMemberImageDAO illusionGroupMemberImageDAO;
  
  @Inject
  private IllusionGroupSettingDAO illusionGroupSettingDAO;

  @Inject
  private IllusionGroupMemberSettingDAO illusionGroupMemberSettingDAO;

  @Inject
  private IllusionFolderDAO illusionFolderDAO;

  @Inject
  private IllusionGroupFolderDAO illusionGroupFolderDAO;
  
  @Inject
  private MaterialController materialController;

  @Inject
  private Event<MemberAddedEvent> memberAddedEvent;

  @Inject
  private Event<MemberRoleChangeEvent> roleChangeEvent;
  
  /* IllusionGroup */

  public IllusionGroup createIllusionGroup(String urlName, String name, String description, String xmppRoom, IllusionGroupFolder folder, IllusionGroupJoinMode joinMode, Date created, Double signUpFee, Currency signUpFeeCurrency) {
    return illusionGroupDAO.create(urlName, name, description, xmppRoom, folder, joinMode, created, signUpFee, signUpFeeCurrency);
  }

  public IllusionGroup findIllusionGroupById(Long id) {
    return illusionGroupDAO.findById(id);
  }

  public IllusionGroup findIllusionGroupByUrlName(String urlName) {
    return illusionGroupDAO.findByUrlName(urlName);
  }

  public List<IllusionGroup> listIllusionGroupsByUserAndRole(User user, IllusionGroupMemberRole role) {
    return illusionGroupMemberDAO.listIllusionGroupsByUserAndRole(user, role);
  }

  /* IllusionGroupMember */
  
  public IllusionGroupMember createIllusionGroupMember(User user, IllusionGroup group, String characterName, IllusionGroupMemberRole role) {
    IllusionGroupMember member = illusionGroupMemberDAO.create(user, group, characterName, role);
    
    String groupUrl = FacesUtils.getLocalAddress(true);
    if (StringUtils.isNotBlank(groupUrl)) {
      groupUrl += "/illusion/group/" + member.getGroup().getUrlName();
    }
    
    memberAddedEvent.fire(new MemberAddedEvent(member.getId(), groupUrl));
    
    return member;
  }

  public IllusionGroupMember findIllusionGroupMemberById(Long memberId) {
    return illusionGroupMemberDAO.findById(memberId);
  }
  
  public IllusionGroupMember findIllusionGroupMemberByUserAndGroup(IllusionGroup group, User user) {
    return illusionGroupMemberDAO.findByGroupAndUser(group, user);
  }
  
  public List<IllusionGroupMember> listIllusionGroupMembersByGroup(IllusionGroup group) {
    return illusionGroupMemberDAO.listByGroup(group);
  }
  
  public List<IllusionGroupMember> listIllusionGroupMembersByGroupAndRole(IllusionGroup group, IllusionGroupMemberRole role) {
    return illusionGroupMemberDAO.listByGroupAndRole(group, role);
  }

  public Long countIllusionGroupMembersByGroupAndRole(IllusionGroup group, IllusionGroupMemberRole role) {
    return illusionGroupMemberDAO.countByGroupAndRole(group, role);
  }
  
  public IllusionGroupMember updateIllusionGroupMemberCharacterName(IllusionGroupMember member, String characterName) {
    return illusionGroupMemberDAO.updateCharacterName(member, characterName);
  }

  public IllusionGroupMember updateIllusionGroupMemberRole(IllusionGroupMember member, IllusionGroupMemberRole role) {
    IllusionGroupMemberRole oldRole = member.getRole();
    IllusionGroupMember groupMember = illusionGroupMemberDAO.updateRole(member, role);
    if (oldRole != role) {
      String groupUrl = FacesUtils.getLocalAddress(true);
      if (StringUtils.isNotBlank(groupUrl)) {
        groupUrl += "/illusion/group/" + member.getGroup().getUrlName();
      }
      
      roleChangeEvent.fire(new MemberRoleChangeEvent(member.getId(), oldRole, role, groupUrl));
    }
    
    return groupMember;
  }

  /* IllusionGroupMemberImage */

  public IllusionGroupMemberImage createIllusionGroupMemberImage(IllusionGroupMember member, String contentType, byte[] data, Date modified) {
    return illusionGroupMemberImageDAO.create(member, contentType, data, modified);
  }

  public IllusionGroupMemberImage findIllusionGroupMemberImageByMember(IllusionGroupMember member) {
    return illusionGroupMemberImageDAO.findByMember(member);
  }
  
  public IllusionGroupMemberImage updateIllusionGroupMemberImage(IllusionGroupMemberImage image, String contentType, byte[] data, Date modified) {
    return illusionGroupMemberImageDAO.updateModified(illusionGroupMemberImageDAO.updateContentType(illusionGroupMemberImageDAO.updateData(image, data), contentType), modified);
  }
  
  /* Settings */
  
  public String getIllusionGroupSettingValue(IllusionGroupMember member, IllusionGroupSettingKey key) {
    IllusionGroupMemberSetting userSetting = illusionGroupMemberSettingDAO.findByMemberAndKey(member, key);
    if ((userSetting != null) && StringUtils.isNotBlank(userSetting.getValue())) {
      return userSetting.getValue();
    }
    
    IllusionGroupSetting groupSetting = illusionGroupSettingDAO.findByUserAndKey(member.getGroup(), key);
    if (groupSetting != null) {
      return groupSetting.getValue();
    }
    
    return null;
  }
  
  public Object getIllusionGroupUserSetting(IllusionGroupMember groupUser, IllusionGroupSettingKey key) {
    switch (key) {
      case DICE:
        return getIllusionGroupDiceSetting(groupUser);
    }
    
    return null;
  }
  
  private <T> T getIllusionGroupSetting(IllusionGroupMember user, IllusionGroupSettingKey key, Class<T> clazz) {
    String value = getIllusionGroupSettingValue(user, key);
    if (StringUtils.isNotBlank(value)) {
      ObjectMapper objectMapper = new ObjectMapper();
      try {
        return objectMapper.readValue(value, clazz);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Could not parse IllusionGroupSetting " + key + " from user: " + user.getId());
      }
    }
    
    return null;
  }
  
  public List<String> getIllusionGroupDiceSetting(IllusionGroupMember user) {
    @SuppressWarnings("unchecked")
    List<String> result = getIllusionGroupSetting(user, IllusionGroupSettingKey.DICE, List.class);
    if (result == null) {
      result = Collections.emptyList();
    }
    
    return result;
  }

  public IllusionGroupMemberSetting setIllusionGroupSettingValue(IllusionGroupMember member, IllusionGroupSettingKey key, String value) {
    IllusionGroupMemberSetting setting = illusionGroupMemberSettingDAO.findByMemberAndKey(member, key);
    if (setting == null) {
      return illusionGroupMemberSettingDAO.create(member, key, value);
    } else {
      return illusionGroupMemberSettingDAO.updateValue(setting, value);
    }
  }

  public Map<IllusionGroupSettingKey, Object> getIllusionGroupUserSettings(IllusionGroupMember groupUser) {
    Map<IllusionGroupSettingKey, Object> result = new HashMap<>();
    
    for (IllusionGroupSettingKey key : IllusionGroupSettingKey.values()) {
      switch (key) {
        case DICE:
          result.put(key, getIllusionGroupUserSetting(groupUser, key));
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

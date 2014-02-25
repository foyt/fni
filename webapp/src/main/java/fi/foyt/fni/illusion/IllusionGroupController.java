package fi.foyt.fni.illusion;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.persistence.dao.illusion.IllusionGroupDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupSettingDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupUserDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupUserImageDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupUserSettingDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserImage;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserRole;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserSetting;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class IllusionGroupController {
  
  @Inject
  private Logger logger;

  @Inject
  private IllusionGroupDAO illusionGroupDAO;

  @Inject
  private IllusionGroupUserDAO illusionGroupUserDAO;

  @Inject
  private IllusionGroupUserImageDAO illusionGroupUserImageDAO;
  
  @Inject
  private IllusionGroupSettingDAO illusionGroupSettingDAO;
  
  @Inject
  private IllusionGroupUserSettingDAO illusionGroupUserSettingDAO;
  
  /* IllusionGroup */

  public IllusionGroup createIllusionGroup(String urlName, String name, String description, String xmppRoom, Date created) {
    return illusionGroupDAO.create(urlName, name, description, xmppRoom, created);
  }

  public IllusionGroup findIllusionGroupById(Long id) {
    return illusionGroupDAO.findById(id);
  }

  public IllusionGroup findIllusionGroupByUrlName(String urlName) {
    return illusionGroupDAO.findByUrlName(urlName);
  }

  public List<IllusionGroup> listIllusionGroupsByUserAndRole(User user, IllusionGroupUserRole role) {
    return illusionGroupUserDAO.listIllusionGroupsByUserAndRole(user, role);
  }

  /* IllusionGroupUser */
  
  public IllusionGroupUser createIllusionGroupUser(User user, IllusionGroup group, String nickname, IllusionGroupUserRole role) {
    return illusionGroupUserDAO.create(user, group, nickname, role);
  }
  
  public IllusionGroupUser findIllusionGroupUserByUserAndGroup(IllusionGroup group, User user) {
    return illusionGroupUserDAO.findByGroupAndUser(group, user);
  }
  
  public List<IllusionGroupUser> listIllusionGroupUsersByGroup(IllusionGroup group) {
    return illusionGroupUserDAO.listByGroup(group);
  }

  public Long countIllusionGroupUsersByGroupAndRole(IllusionGroup group, IllusionGroupUserRole role) {
    return illusionGroupUserDAO.countByGroupAndRole(group, role);
  }
  
  public IllusionGroupUser updateIllusionGroupUserNickname(IllusionGroupUser illusionGroupUser, String nickname) {
    return illusionGroupUserDAO.updateNickname(illusionGroupUser, nickname);
  }
  
  /* IllusionGroupUserImage */

  public IllusionGroupUserImage createIllusionGroupUserImage(IllusionGroupUser user, String contentType, byte[] data, Date modified) {
    return illusionGroupUserImageDAO.create(user, contentType, data, modified);
  }

  public IllusionGroupUserImage findIllusionGroupUserImageByUser(IllusionGroupUser user) {
    return illusionGroupUserImageDAO.findByUser(user);
  }
  
  public IllusionGroupUserImage updateIllusionGroupUserImage(IllusionGroupUserImage image, String contentType, byte[] data, Date modified) {
    return illusionGroupUserImageDAO.updateModified(illusionGroupUserImageDAO.updateContentType(illusionGroupUserImageDAO.updateData(image, data), contentType), modified);
  }
  
  /* Settings */
  
  public String getIllusionGroupSettingValue(IllusionGroupUser user, IllusionGroupSettingKey key) {
    IllusionGroupUserSetting userSetting = illusionGroupUserSettingDAO.findByUserAndKey(user, key);
    if ((userSetting != null) && StringUtils.isNotBlank(userSetting.getValue())) {
      return userSetting.getValue();
    }
    
    IllusionGroupSetting groupSetting = illusionGroupSettingDAO.findByUserAndKey(user.getGroup(), key);
    if (groupSetting != null) {
      return groupSetting.getValue();
    }
    
    return null;
  }
  
  public <T> T getIllusionGroupSetting(IllusionGroupUser user, IllusionGroupSettingKey key, Class<T> clazz) {
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
  
  public List<String> getIllusionGroupDiceSetting(IllusionGroupUser user) {
    @SuppressWarnings("unchecked")
    List<String> result = getIllusionGroupSetting(user, IllusionGroupSettingKey.DICE, List.class);
    if (result == null) {
      result = Collections.emptyList();
    }
    
    return result;
  }
  
}

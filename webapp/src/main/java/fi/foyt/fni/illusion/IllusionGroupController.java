package fi.foyt.fni.illusion;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.illusion.IllusionGroupDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupUserDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionGroupUserImageDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserImage;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserRole;
import fi.foyt.fni.persistence.model.users.User;

@Dependent
@Stateless
public class IllusionGroupController {

  @Inject
  private IllusionGroupDAO illusionGroupDAO;

  @Inject
  private IllusionGroupUserDAO illusionGroupUserDAO;

  @Inject
  private IllusionGroupUserImageDAO illusionGroupUserImageDAO;
  
  /* IllusionGroup */

  public IllusionGroup createIllusionGroup(String urlName, String name, String description, String xmppRoom) {
    return illusionGroupDAO.create(urlName, name, description, xmppRoom);
  }

  public IllusionGroup findIllusionGroupById(Long id) {
    return illusionGroupDAO.findById(id);
  }

  public IllusionGroup findIllusionGroupByUrlName(String urlName) {
    return illusionGroupDAO.findByUrlName(urlName);
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
  
}

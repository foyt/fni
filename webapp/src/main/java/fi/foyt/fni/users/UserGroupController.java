package fi.foyt.fni.users;

import java.io.Serializable;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.users.UserGroupDAO;
import fi.foyt.fni.persistence.model.users.UserGroup;

public class UserGroupController implements Serializable {
  
  private static final long serialVersionUID = 5659999217721474844L;

  @Inject
  private UserGroupDAO userGroupDAO;
  
  public UserGroup findUserGroupById(Long userGroupId) {
    return userGroupDAO.findById(userGroupId);
  }

}

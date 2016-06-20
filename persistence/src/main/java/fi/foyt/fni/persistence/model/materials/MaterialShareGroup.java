package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.UserGroup;

@Entity
public class MaterialShareGroup extends MaterialShare {

  public UserGroup getUserGroup() {
    return userGroup;
  }
  
  public void setUserGroup(UserGroup userGroup) {
    this.userGroup = userGroup;
  }
  
  @ManyToOne (optional = false)
  private UserGroup userGroup;
}
package fi.foyt.fni.rest.material.model;

import fi.foyt.fni.persistence.model.materials.MaterialRole;

public class MaterialShareUserGroup extends MaterialShare {

  public MaterialShareUserGroup() {
    super();
  }

  public MaterialShareUserGroup(Long id, Long userGroupId, MaterialRole role) {
    super(id, role);
    this.userGroupId = userGroupId;
  }

  public Long getUserGroupId() {
    return userGroupId;
  }
  
  public void setUserGroupId(Long userGroupId) {
    this.userGroupId = userGroupId;
  }

  private Long userGroupId;
}

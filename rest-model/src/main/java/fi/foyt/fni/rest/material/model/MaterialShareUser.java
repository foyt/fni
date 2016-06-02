package fi.foyt.fni.rest.material.model;

import fi.foyt.fni.persistence.model.materials.MaterialRole;

public class MaterialShareUser extends MaterialShare {

  public MaterialShareUser() {
    super();
  }

  public MaterialShareUser(Long id, Long userId, MaterialRole role) {
    super(id, role);
    this.userId = userId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  private Long userId;
}

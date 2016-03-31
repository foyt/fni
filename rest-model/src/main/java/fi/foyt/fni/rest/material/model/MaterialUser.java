package fi.foyt.fni.rest.material.model;

import fi.foyt.fni.persistence.model.materials.MaterialRole;

public class MaterialUser {

  public MaterialUser() {
  }

  public MaterialUser(Long id, Long userId, MaterialRole role) {
    super();
    this.id = id;
    this.userId = userId;
    this.role = role;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public MaterialRole getRole() {
    return role;
  }

  public void setRole(MaterialRole role) {
    this.role = role;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  private Long id;
  private Long userId;
  private MaterialRole role;
}

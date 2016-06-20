package fi.foyt.fni.rest.material.model;

import fi.foyt.fni.persistence.model.materials.MaterialRole;

public abstract class MaterialShare {

  public MaterialShare() {
  }

  public MaterialShare(Long id, MaterialRole role) {
    super();
    this.id = id;
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

  private Long id;
  private MaterialRole role;
}

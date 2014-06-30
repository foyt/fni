package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import fi.foyt.fni.persistence.model.materials.Material;

@Entity 
@PrimaryKeyJoinColumn (name="id")
public class IllusionGroupForgeMaterial extends IllusionGroupMaterial {

  public Material getForgeMaterial() {
    return forgeMaterial;
  }
  
  public void setForgeMaterial(Material forgeMaterial) {
    this.forgeMaterial = forgeMaterial;
  }
  
  @ManyToOne
  private Material forgeMaterial;
}

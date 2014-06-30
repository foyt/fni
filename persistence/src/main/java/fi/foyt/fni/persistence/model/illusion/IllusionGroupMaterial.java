package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

@Entity
@Inheritance (strategy=InheritanceType.JOINED)
public class IllusionGroupMaterial {

  public Long getId() {
    return id;
  }
  
  public IllusionGroup getGroup() {
    return group;
  }
  
  public void setGroup(IllusionGroup group) {
    this.group = group;
  }
  
  public IllusionGroupMaterialType getType() {
    return type;
  }
  
  public void setType(IllusionGroupMaterialType type) {
    this.type = type;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private IllusionGroup group;
  
  @Enumerated (EnumType.STRING)
  @Column (nullable = false)
  private IllusionGroupMaterialType type;
}
package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Cacheable (true)
public class MaterialRevisionSetting {

  public Long getId() {
    return id;
  }

  public MaterialRevision getMaterialRevision() {
	  return materialRevision;
  }
  
  public void setMaterialRevision(MaterialRevision materialRevision) {
	  this.materialRevision = materialRevision;
  }
  
  public MaterialSettingKey getKey() {
	  return key;
  }
  
  public void setKey(MaterialSettingKey key) {
	  this.key = key;
  }
  
  public String getValue() {
	  return value;
  }
  
  public void setValue(String value) {
	  this.value = value;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private MaterialRevision materialRevision;
  
  @ManyToOne
  private MaterialSettingKey key;

  @Column
  private String value;
}
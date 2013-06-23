package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class MaterialSetting {

  public Long getId() {
    return id;
  }

  public Material getMaterial() {
    return material;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
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
  private Material material;
  
  @ManyToOne
  private MaterialSettingKey key;

  @Column (nullable = false)
  @NotEmpty
  private String value;
}
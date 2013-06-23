package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class MaterialSettingKey {

  public Long getId() {
    return id;
  }
  
  public String getName() {
	  return name;
  }
  
  public void setName(String name) {
	  this.name = name;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable = false)
  @NotEmpty
  private String name;
}
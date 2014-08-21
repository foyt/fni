package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class IllusionEventSetting {

  public Long getId() {
    return id;
  }
  
  public IllusionEvent getGroup() {
    return group;
  }
  
  public void setGroup(IllusionEvent group) {
    this.group = group;
  }
  
  public IllusionEventSettingKey getKey() {
    return key;
  }
  
  public void setKey(IllusionEventSettingKey key) {
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
  private IllusionEvent group;
  
  @Enumerated (EnumType.STRING)
  @NotNull
  @Column (nullable = false, name="settingKey")
  private IllusionEventSettingKey key;
  
  private String value;
}
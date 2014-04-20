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
public class IllusionGroupSetting {

  public Long getId() {
    return id;
  }
  
  public IllusionGroup getGroup() {
    return group;
  }
  
  public void setGroup(IllusionGroup group) {
    this.group = group;
  }
  
  public IllusionGroupSettingKey getKey() {
    return key;
  }
  
  public void setKey(IllusionGroupSettingKey key) {
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
  private IllusionGroup group;
  
  @Enumerated (EnumType.STRING)
  @NotNull
  @Column (nullable = false, name="settingKey")
  private IllusionGroupSettingKey key;
  
  private String value;
}
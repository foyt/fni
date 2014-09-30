package fi.foyt.fni.illusion.rest;

import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;

public class IllusionEventMaterialParticipantSetting {

  public IllusionEventMaterialParticipantSetting() {
  }
  
  public IllusionEventMaterialParticipantSetting(Long id, IllusionEventMaterialParticipantSettingKey key, String value) {
    this.id = id;
    this.key = key;
    this.value = value;
  }
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public IllusionEventMaterialParticipantSettingKey getKey() {
    return key;
  }
  
  public void setKey(IllusionEventMaterialParticipantSettingKey key) {
    this.key = key;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  private Long id;
  private IllusionEventMaterialParticipantSettingKey key;
  private String value;
}

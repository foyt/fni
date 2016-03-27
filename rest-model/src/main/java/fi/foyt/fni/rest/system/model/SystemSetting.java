package fi.foyt.fni.rest.system.model;

import fi.foyt.fni.persistence.model.system.SystemSettingKey;

public class SystemSetting {

  public SystemSetting() {
  }
  
  public SystemSetting(SystemSettingKey key, String value) {
    super();
    this.key = key;
    this.value = value;
  }

  public SystemSettingKey getKey() {
    return key;
  }

  public void setKey(SystemSettingKey key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  private SystemSettingKey key;
  private String value;
}

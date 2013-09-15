package fi.foyt.fni.persistence.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SystemSetting {

  public Long getId() {
    return id;
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

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @Enumerated (EnumType.STRING)
  private SystemSettingKey key;

  @Column(nullable = false)
  private String value;
}

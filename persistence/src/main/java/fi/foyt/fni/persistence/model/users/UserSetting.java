package fi.foyt.fni.persistence.model.users;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class UserSetting {

  public Long getId() {
    return id;
  }
 
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public UserSettingKey getUserSettingKey() {
    return userSettingKey;
  }
  
  public void setUserSettingKey(UserSettingKey userSettingKey) {
    this.userSettingKey = userSettingKey;
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
  private User user;
 
  @Column (nullable = false)
  private String value;

  @Column (nullable = false)
  @NotNull
  @Enumerated (EnumType.STRING)
  private UserSettingKey userSettingKey;
}
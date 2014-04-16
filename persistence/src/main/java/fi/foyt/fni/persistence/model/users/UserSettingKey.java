package fi.foyt.fni.persistence.model.users;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class UserSettingKey {

  public Long getId() {
    return id;
  }
 
  public String getKey() {
    return key;
  }
  
  public void setKey(String key) {
    this.key = key;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @Column (nullable = false, name = "key_")
  private String key;
}
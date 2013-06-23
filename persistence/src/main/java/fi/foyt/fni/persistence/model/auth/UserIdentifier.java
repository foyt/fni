package fi.foyt.fni.persistence.model.auth;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class UserIdentifier {

	public Long getId() {
    return id;
  }
 
  public String getIdentifier() {
    return identifier;
  }
 
  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getSourceId() {
	  return sourceId;
  }
  
  public void setSourceId(String sourceId) {
	  this.sourceId = sourceId;
  }
  
  public AuthSource getAuthSource() {
	  return authSource;
  }
  
  public void setAuthSource(AuthSource authSource) {
	  this.authSource = authSource;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private User user;
 
  @Column (nullable = false)
  @NotEmpty
  private String sourceId;

  @Column (nullable = false)
  @NotEmpty
  private String identifier;
  
  @Column (nullable = false)
  @Enumerated (EnumType.STRING)
  private AuthSource authSource;
}
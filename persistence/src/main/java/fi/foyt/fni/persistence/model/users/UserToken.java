package fi.foyt.fni.persistence.model.users;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.auth.UserIdentifier;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class UserToken {

  public Long getId() {
    return id;
  }
 
  public String getToken() {
    return token;
  }
  
  public void setToken(String token) {
    this.token = token;
  }
  
  public String getSecret() {
    return secret;
  }
  
  public void setSecret(String secret) {
    this.secret = secret;
  }
  
  public UserIdentifier getUserIdentifier() {
    return userIdentifier;
  }
  
  public void setUserIdentifier(UserIdentifier userIdentifier) {
    this.userIdentifier = userIdentifier;
  }
  
  public Date getExpires() {
    return expires;
  }
  
  public void setExpires(Date expires) {
    this.expires = expires;
  }
  
  public String getGrantedScopes() {
    return grantedScopes;
  }

  public void setGrantedScopes(String grantedScopes) {
    this.grantedScopes = grantedScopes;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private UserIdentifier userIdentifier;
 
  @Column (nullable = false, length = 1024)
  @NotNull
  @NotEmpty
  private String token;

  private String secret;
  
  private String grantedScopes;
  
  private Date expires;

}
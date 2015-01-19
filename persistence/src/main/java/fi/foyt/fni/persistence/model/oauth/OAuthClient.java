package fi.foyt.fni.persistence.model.oauth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class OAuthClient {

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }
  
  public String getRedirectUrl() {
    return redirectUrl;
  }
  
  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }
  
  public OAuthClientType getType() {
    return type;
  }
  
  public void setType(OAuthClientType type) {
    this.type = type;
  }
  
  public User getServiceUser() {
    return serviceUser;
  }
  
  public void setServiceUser(User serviceUser) {
    this.serviceUser = serviceUser;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String name;

  @NotNull
  @NotEmpty
  @Column(nullable = false, unique = true)
  private String clientId;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String clientSecret;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String redirectUrl;
  
  @NotNull
  @Column(nullable = false)
  @Enumerated (EnumType.STRING)
  private OAuthClientType type;

  @ManyToOne
  private User serviceUser;
}

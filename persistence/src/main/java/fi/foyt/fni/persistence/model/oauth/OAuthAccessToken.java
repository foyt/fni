package fi.foyt.fni.persistence.model.oauth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class OAuthAccessToken {

  public Long getId() {
    return id;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public Long getExpires() {
    return expires;
  }

  public void setExpires(Long expires) {
    this.expires = expires;
  }

  public OAuthAuthorizationCode getAuthorizationCode() {
    return authorizationCode;
  }

  public void setAuthorizationCode(OAuthAuthorizationCode authorizationCode) {
    this.authorizationCode = authorizationCode;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @NotEmpty
  @Column(nullable = false, unique = true)
  private String accessToken;

  @NotNull
  @Column(nullable = false)
  private Long expires;

  @NotNull
  @ManyToOne
  @JoinColumn(nullable = false)
  private OAuthAuthorizationCode authorizationCode;
}

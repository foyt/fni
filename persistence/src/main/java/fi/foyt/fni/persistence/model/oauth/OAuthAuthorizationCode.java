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

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class OAuthAuthorizationCode {

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public OAuthClient getClient() {
    return client;
  }

  public void setClient(OAuthClient client) {
    this.client = client;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @ManyToOne
  @JoinColumn(nullable = false)
  private User user;

  @NotNull
  @ManyToOne
  @JoinColumn(nullable = false)
  private OAuthClient client;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String code;
}

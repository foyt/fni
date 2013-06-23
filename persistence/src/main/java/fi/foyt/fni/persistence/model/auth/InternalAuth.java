package fi.foyt.fni.persistence.model.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class InternalAuth {

  public Long getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Boolean getVerified() {
	  return verified;
  }
  
  public void setVerified(Boolean verified) {
	  this.verified = verified;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @NotNull
  private String password;

  @ManyToOne 
  private User user;
  
  @Column (nullable=false, columnDefinition = "BIT")
  @NotNull
  private Boolean verified;
}

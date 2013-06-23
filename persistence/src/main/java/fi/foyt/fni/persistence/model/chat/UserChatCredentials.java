package fi.foyt.fni.persistence.model.chat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class UserChatCredentials {

  public Long getId() {
    return id;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getUserJid() {
    return userJid;
  }
  
  public void setUserJid(String userJid) {
    this.userJid = userJid;
  }
  
  public String getPassword() {
    return password;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;

  @ManyToOne 
  private User user;

  @Column(nullable = false)
  private String userJid;

  @Column(nullable = false)
  private String password;
}
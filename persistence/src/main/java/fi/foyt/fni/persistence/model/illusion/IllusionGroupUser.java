package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class IllusionGroupUser {

  public Long getId() {
    return id;
  }
  
  public String getNickname() {
    return nickname;
  }
  
  public void setNickname(String nickname) {
    this.nickname = nickname;
  }
  
  public IllusionGroupUserRole getRole() {
    return role;
  }
  
  public void setRole(IllusionGroupUserRole role) {
    this.role = role;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public IllusionGroup getGroup() {
    return group;
  }
  
  public void setGroup(IllusionGroup group) {
    this.group = group;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  private String nickname;

  @ManyToOne
  private User user;
  
  @ManyToOne
  private IllusionGroup group;
  
  @NotNull
  @Enumerated (EnumType.STRING)
  private IllusionGroupUserRole role;
}
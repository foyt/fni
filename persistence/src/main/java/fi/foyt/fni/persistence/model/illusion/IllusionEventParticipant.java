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
public class IllusionEventParticipant {

  public Long getId() {
    return id;
  }
  
  public String getCharacterName() {
    return characterName;
  }
  
  public void setCharacterName(String characterName) {
    this.characterName = characterName;
  }
  
  public IllusionEventParticipantRole getRole() {
    return role;
  }
  
  public void setRole(IllusionEventParticipantRole role) {
    this.role = role;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public IllusionEvent getEvent() {
    return event;
  }
  
  public void setEvent(IllusionEvent event) {
    this.event = event;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  private String characterName;

  @ManyToOne
  private User user;
  
  @ManyToOne
  private IllusionEvent event;
  
  @NotNull
  @Enumerated (EnumType.STRING)
  private IllusionEventParticipantRole role;
}
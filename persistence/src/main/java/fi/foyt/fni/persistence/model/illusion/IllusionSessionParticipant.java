package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class IllusionSessionParticipant {

  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public IllusionSession getSession() {
    return session;
  }
  
  public void setSession(IllusionSession session) {
    this.session = session;
  }
  
  public User getParticipant() {
    return participant;
  }
  
  public void setParticipant(User participant) {
    this.participant = participant;
  }
  
  public IllusionSessionParticipantRole getRole() {
    return role;
  }
  
  public void setRole(IllusionSessionParticipantRole role) {
    this.role = role;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private User participant;
  
  @ManyToOne
  private IllusionSession session;

  @Enumerated (EnumType.STRING)
  private IllusionSessionParticipantRole role;
}

package fi.foyt.fni.rest.illusion.model;

import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;

public class IllusionEventParticipant {
  
  public IllusionEventParticipant() {
  }

  public IllusionEventParticipant(Long id, Long userId, IllusionEventParticipantRole role, String displayName) {
    super();
    this.id = id;
    this.userId = userId;
    this.role = role;
    this.displayName = displayName;
  }

  /**
   * Returns event participant id
   * 
   * @return event participant id
   */
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns event participant role
   * 
   * @return event participant role
   */
  public IllusionEventParticipantRole getRole() {
    return role;
  }

  public void setRole(IllusionEventParticipantRole role) {
    this.role = role;
  }

  /**
   * Returns event participant' user id
   * 
   * @return event participant' user id
   */
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  
  /**
   * Returns participant's display name. Defaults to users nickname / full name if nulled when creating a participant
   * 
   * @return participant's display name. Defaults to users nickname / full name if nulled when creating a participant
   */
  public String getDisplayName() {
    return displayName;
  }
  
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  private Long id;
  private Long userId;
  private IllusionEventParticipantRole role;
  private String displayName;
}

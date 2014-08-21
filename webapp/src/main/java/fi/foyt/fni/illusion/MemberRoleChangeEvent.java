package fi.foyt.fni.illusion;

import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;

public class MemberRoleChangeEvent {

  public MemberRoleChangeEvent(Long memberId, IllusionEventParticipantRole oldRole, IllusionEventParticipantRole newRole) {
    super();
    this.memberId = memberId;
    this.oldRole = oldRole;
    this.newRole = newRole;
  }
  
  public Long getMemberId() {
    return memberId;
  }
  
  public IllusionEventParticipantRole getNewRole() {
    return newRole;
  }
  
  public IllusionEventParticipantRole getOldRole() {
    return oldRole;
  }

  private Long memberId;
  private IllusionEventParticipantRole oldRole;
  private IllusionEventParticipantRole newRole;
}

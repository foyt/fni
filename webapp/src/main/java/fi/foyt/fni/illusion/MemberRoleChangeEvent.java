package fi.foyt.fni.illusion;

import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;

public class MemberRoleChangeEvent {

  public MemberRoleChangeEvent(Long memberId, IllusionGroupMemberRole oldRole, IllusionGroupMemberRole newRole) {
    super();
    this.memberId = memberId;
    this.oldRole = oldRole;
    this.newRole = newRole;
  }
  
  public Long getMemberId() {
    return memberId;
  }
  
  public IllusionGroupMemberRole getNewRole() {
    return newRole;
  }
  
  public IllusionGroupMemberRole getOldRole() {
    return oldRole;
  }

  private Long memberId;
  private IllusionGroupMemberRole oldRole;
  private IllusionGroupMemberRole newRole;
}

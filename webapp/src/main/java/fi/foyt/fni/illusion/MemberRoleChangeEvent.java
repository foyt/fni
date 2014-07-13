package fi.foyt.fni.illusion;

import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;

public class MemberRoleChangeEvent {

  public MemberRoleChangeEvent(Long memberId, IllusionGroupMemberRole oldRole, IllusionGroupMemberRole newRole, String groupUrl) {
    super();
    this.memberId = memberId;
    this.oldRole = oldRole;
    this.newRole = newRole;
    this.groupUrl = groupUrl;
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
  
  public String getGroupUrl() {
    return groupUrl;
  }

  private Long memberId;
  private IllusionGroupMemberRole oldRole;
  private IllusionGroupMemberRole newRole;
  private String groupUrl;
}

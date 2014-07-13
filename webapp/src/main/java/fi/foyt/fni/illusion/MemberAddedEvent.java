package fi.foyt.fni.illusion;

public class MemberAddedEvent {

  public MemberAddedEvent(Long memberId, String groupUrl) {
    super();
    this.memberId = memberId;
    this.groupUrl = groupUrl;
  }
  
  public Long getMemberId() {
    return memberId;
  }
  
  public String getGroupUrl() {
    return groupUrl;
  }

  private Long memberId;
  private String groupUrl;
}

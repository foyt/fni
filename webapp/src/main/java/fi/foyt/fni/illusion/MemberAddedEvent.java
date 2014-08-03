package fi.foyt.fni.illusion;

public class MemberAddedEvent {

  public MemberAddedEvent(Long memberId) {
    super();
    this.memberId = memberId;
  }
  
  public Long getMemberId() {
    return memberId;
  }

  private Long memberId;
}

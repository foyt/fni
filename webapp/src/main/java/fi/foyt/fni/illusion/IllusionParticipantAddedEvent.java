package fi.foyt.fni.illusion;

public class IllusionParticipantAddedEvent {

  public IllusionParticipantAddedEvent(Long memberId) {
    super();
    this.memberId = memberId;
  }
  
  public Long getMemberId() {
    return memberId;
  }

  private Long memberId;
}

package fi.foyt.fni.coops;

public class CoOpsSessionJoinEvent {

  public CoOpsSessionJoinEvent(String sessionId) {
    super();
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  private String sessionId;
}

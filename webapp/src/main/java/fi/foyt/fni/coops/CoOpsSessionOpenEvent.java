package fi.foyt.fni.coops;

public class CoOpsSessionOpenEvent {

  public CoOpsSessionOpenEvent(String sessionId) {
    super();
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  private String sessionId;
}

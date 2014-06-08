package fi.foyt.fni.coops;

public class CoOpsSessionCloseEvent {

  public CoOpsSessionCloseEvent(String sessionId) {
    super();
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  private String sessionId;
}

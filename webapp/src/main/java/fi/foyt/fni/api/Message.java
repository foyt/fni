package fi.foyt.fni.api;

public class Message {

  public Message(String message, Severity severity) {
    this.message = message;
    this.severity = severity;
  }
  
  public String getMessage() {
    return message;
  }
  
  public Severity getSeverity() {
    return severity;
  }
  
  private String message;
  private Severity severity;
}

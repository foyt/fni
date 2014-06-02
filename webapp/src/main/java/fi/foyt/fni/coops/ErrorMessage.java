package fi.foyt.fni.coops;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessage {
  
  public ErrorMessage() {
  }
  
  public ErrorMessage(String type, int code, String message) {
    this.type = type;
    this.data = new HashMap<>();
    this.data.put("code", String.valueOf(code));
    this.data.put("message", message);
  }
  
  public Map<String, String> getData() {
    return data;
  }
  
  public String getType() {
    return type;
  }
  
  private Map<String, String> data;
  private String type;
}
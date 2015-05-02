package fi.foyt.fni.larpkalenteri;


import java.util.Map;

public class Type {
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public Map<String, String> getName() {
    return name;
  }
  
  public void setName(Map<String, String> name) {
    this.name = name;
  }
  
  private String id;
  private Map<String, String> name;
}

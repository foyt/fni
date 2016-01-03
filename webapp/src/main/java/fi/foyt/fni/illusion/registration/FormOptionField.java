package fi.foyt.fni.illusion.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FormOptionField {
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public String getLabel() {
    return label;
  }
  
  public void setLabel(String label) {
    this.label = label;
  }
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }

  private String id;
  private String label;
  private String type;
}

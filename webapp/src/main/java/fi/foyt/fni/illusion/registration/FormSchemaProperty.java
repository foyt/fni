package fi.foyt.fni.illusion.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FormSchemaProperty {
  
  public String getType() {
    return type;
  }
  
  public void setType(String type) {
    this.type = type;
  }
  
  public Boolean getRequired() {
    return required;
  }
  
  public void setRequired(Boolean required) {
    this.required = required;
  }
  
  private String type;
  private Boolean required;
  
}

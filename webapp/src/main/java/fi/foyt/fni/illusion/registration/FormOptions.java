package fi.foyt.fni.illusion.registration;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FormOptions {
  
  public Map<String, FormOptionField> getFields() {
    return fields;
  }
  
  public void setFields(Map<String, FormOptionField> fields) {
    this.fields = fields;
  }
  
  private Map<String, FormOptionField> fields;
}

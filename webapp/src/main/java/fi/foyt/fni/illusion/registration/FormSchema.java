package fi.foyt.fni.illusion.registration;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FormSchema {
  
  public Map<String, FormSchemaProperty> getProperties() {
    return properties;
  }
  
  public void setProperties(Map<String, FormSchemaProperty> properties) {
    this.properties = properties;
  }
  
  private Map<String, FormSchemaProperty> properties;
}

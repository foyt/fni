package fi.foyt.fni.illusion.registration;

import java.util.SortedMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FormSchema {
  
  public SortedMap<String, FormSchemaProperty> getProperties() {
    return properties;
  }
  
  public void setProperties(SortedMap<String, FormSchemaProperty> properties) {
    this.properties = properties;
  }
  
  private SortedMap<String, FormSchemaProperty> properties;
}

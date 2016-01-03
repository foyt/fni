package fi.foyt.fni.illusion.registration;

import java.util.SortedMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FormOptions {
  
  public SortedMap<String, FormOptionField> getFields() {
    return fields;
  }
  
  public void setFields(SortedMap<String, FormOptionField> fields) {
    this.fields = fields;
  }
  
  private SortedMap<String, FormOptionField> fields;
}

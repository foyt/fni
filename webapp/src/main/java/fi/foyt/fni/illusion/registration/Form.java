package fi.foyt.fni.illusion.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class Form {
  
  public FormSchema getSchema() {
    return schema;
  }
  
  public void setSchema(FormSchema schema) {
    this.schema = schema;
  }
  
  public FormOptions getOptions() {
    return options;
  }
  
  public void setOptions(FormOptions options) {
    this.options = options;
  }
  
  private FormSchema schema;
  private FormOptions options;
}

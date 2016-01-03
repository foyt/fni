package fi.foyt.fni.illusion.registration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FormReader {

  public FormReader(String formJson) {
    form = parseRegistrationForm(formJson);
  }
  
  public Form getForm() {
    return form;
  }
  
  public String getEmailField() {
    String emailField = findOptionFieldNameById("email");
    if (StringUtils.isNotBlank(emailField)) {
      return emailField;
    }
    
    SortedMap<String, FormOptionField> fields = getOptionFields();
    
    for (String fieldName : fields.keySet()) {
      FormOptionField optionField = fields.get(fieldName);
      if ("email".equalsIgnoreCase(optionField.getType())) {
        return fieldName;
      }
    }
    
    return null;
  }
  
  public String getFirstNameField() {
    return findOptionFieldNameById("firstname");
  }

  public String getLastNameField() {
    return findOptionFieldNameById("lastname");
  }
  
  public List<String> getFields() {
    List<String> result = new ArrayList<>();
    
    SortedMap<String, FormSchemaProperty> schemaProperties = getSchemaProperties();
    for (String fieldName : schemaProperties.keySet()) {
      result.add(fieldName);
    }
    
    return result;
  }
  
  public List<String> getRequiredFields() {
    List<String> result = new ArrayList<>();
    
    SortedMap<String, FormSchemaProperty> schemaProperties = getSchemaProperties();
    for (String fieldName : schemaProperties.keySet()) {
      FormSchemaProperty schemaProperty = schemaProperties.get(fieldName);
      if (Boolean.TRUE.equals(schemaProperty.getRequired())) {
        result.add(fieldName);
      }
    }
    
    return result;
  }

  public SortedMap<String, FormOptionField> getOptionFields() {
    FormOptions options = getForm().getOptions();
    if (options == null) {
      return Collections.emptySortedMap();
    }
    
    SortedMap<String, FormOptionField> fields = options.getFields();
    if (fields == null) {
      return Collections.emptySortedMap();
    }
    
    return fields;
  }
  
  public String findOptionFieldNameById(String id) {
    SortedMap<String, FormOptionField> optionFields = getOptionFields();
    for (String fieldName : optionFields.keySet()) {
      FormOptionField optionField = optionFields.get(fieldName);
      if (id.equals(optionField.getId())) {
        return fieldName;
      }
    }
    
    return null;
  }

  public SortedMap<String, FormSchemaProperty> getSchemaProperties() {
    FormSchema schema = getForm().getSchema();
    if (schema == null) {
      return Collections.emptySortedMap();
    }
    
    SortedMap<String, FormSchemaProperty> properties = schema.getProperties();
    if (properties == null) {
      return Collections.emptySortedMap();
    }
    
    return properties;
  }

  public String getFieldLabel(String field) {
    String result = null;
    
    FormOptionField optionField = getOptionField(field);
    if (optionField != null) {
      result = optionField.getLabel();
    }

    if (StringUtils.isBlank(result)) {
      result = field;
    }

    return result;
  }
  
  public FormOptionField getOptionField(String fieldName) {
    return getOptionFields().get(fieldName);
  }

  private Form parseRegistrationForm(String formJson) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      return objectMapper.readValue(formJson, Form.class);
    } catch (IOException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, String.format("Could not parse registration form"), e);
      return null;
    }
  }
  
  private Form form;
}

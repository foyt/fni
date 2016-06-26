package fi.foyt.fni.illusion.registration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
  
  public Exception getParseError() {
    return parseError;
  }
  
  public String getEmailField() {
    String emailField = findOptionFieldNameById("email");
    if (StringUtils.isNotBlank(emailField)) {
      return emailField;
    }
    
    Map<String, FormOptionField> fields = getOptionFields();
    
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
  
  public List<String> getFields(boolean sort) {
    List<String> fields = getFields();
    if (!sort) {
      return fields;
    }
    
    final Map<String, Integer> orders = new HashMap<>();
    for (String field : fields) {
      FormOptionField option = getOptionField(field);
      if (option != null && option.getOrder() != null) {
        orders.put(field, option.getOrder());
      } else {
        orders.put(field, Integer.MAX_VALUE);
      }
    }
    
    Collections.sort(fields, new Comparator<String>() {      
      @Override
      public int compare(String field1, String field2) {
        return orders.get(field1).compareTo(orders.get(field2));
      }
    });
    
    return fields;
  }
  
  public List<String> getFields() {
    List<String> result = new ArrayList<>();
    
    Map<String, FormSchemaProperty> schemaProperties = getSchemaProperties();
    for (String fieldName : schemaProperties.keySet()) {
      result.add(fieldName);
    }
    
    return result;
  }
  
  public List<String> getRequiredFields() {
    List<String> result = new ArrayList<>();
    
    Map<String, FormSchemaProperty> schemaProperties = getSchemaProperties();
    for (String fieldName : schemaProperties.keySet()) {
      FormSchemaProperty schemaProperty = schemaProperties.get(fieldName);
      if (Boolean.TRUE.equals(schemaProperty.getRequired())) {
        result.add(fieldName);
      }
    }
    
    return result;
  }

  public Map<String, FormOptionField> getOptionFields() {
    FormOptions options = getForm().getOptions();
    if (options == null) {
      return Collections.emptyMap();
    }
    
    Map<String, FormOptionField> fields = options.getFields();
    if (fields == null) {
      return Collections.emptyMap();
    }
    
    return fields;
  }
  
  public String findOptionFieldNameById(String id) {
    Map<String, FormOptionField> optionFields = getOptionFields();
    for (String fieldName : optionFields.keySet()) {
      FormOptionField optionField = optionFields.get(fieldName);
      if (id.equals(optionField.getId())) {
        return fieldName;
      }
    }
    
    return null;
  }

  public Map<String, FormSchemaProperty> getSchemaProperties() {
    FormSchema schema = getForm().getSchema();
    if (schema == null) {
      return Collections.emptyMap();
    }
    
    Map<String, FormSchemaProperty> properties = schema.getProperties();
    if (properties == null) {
      return Collections.emptyMap();
    }
    
    return properties;
  }

  public FormSchemaProperty getSchemaProperty(String fieldName) {
    return getSchemaProperties().get(fieldName);
  }
  
  public boolean isRequiredField(String fieldName) {
    FormSchemaProperty schemaProperty = getSchemaProperty(fieldName);
    if (schemaProperty != null) {
      return schemaProperty.getRequired();
    }
    
    return false;
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
    } catch (Exception e) {
      parseError = e;
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Could not parse registration form", e);
      return null;
    }
  }
  
  private Form form;
  private Exception parseError; 
}

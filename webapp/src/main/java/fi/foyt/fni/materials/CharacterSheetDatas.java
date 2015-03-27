package fi.foyt.fni.materials;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

public class CharacterSheetDatas {

  public CharacterSheetDatas() {
    keys = new HashSet<>();
    values = new HashMap<>();
    dataTypes = new HashMap<>();
  }
  
  public Set<String> getKeys() {
    return keys;
  }
  
  public Set<Long> getUserIds() {
    return Collections.unmodifiableSet(values.keySet());
  }
  
  public Object getValue(String key, Long userId) {
     switch (getDataType(key)) {
       case NUMBER:
         return getDouble(key, userId);
       default:
         return getText(key, userId);
     }
  } 
  
  public String getText(String key, Long userId) {
    return getUserValues(userId).get(key);
  }
  
  public Double getDouble(String key, Long userId) {
    return NumberUtils.createDouble(getText(key, userId));
  }
  
  public DataType getDataType(String key) {
    return dataTypes.get(key);
  }
  
  public void setValue(String key, Long userId, String value) {
    if (!keys.contains(key)) {
      keys.add(key);
    }
    
    if (StringUtils.isNotBlank(value)) {
      boolean numeric = StringUtils.isNumeric(value);
      if (!dataTypes.containsKey(key)) {
        dataTypes.put(key, numeric ? DataType.NUMBER : DataType.TEXT);
      } else {
        dataTypes.put(key, dataTypes.get(key) == DataType.NUMBER && numeric ? DataType.NUMBER : DataType.TEXT);
      }
    }
    
    getUserValues(userId).put(key, value);
  }
  
  private Map<String, String> getUserValues(Long userId) {
    if (!values.containsKey(userId)) {
      this.values.put(userId, new HashMap<String, String>());
    }
    
    return this.values.get(userId);
  }
  
  private Set<String> keys;
  private Map<Long, Map<String, String>> values;
  private Map<String, DataType> dataTypes;
  
  public enum DataType {
    TEXT,
    NUMBER
  }
}

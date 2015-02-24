package fi.foyt.fni.illusion;

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
  
  public Set<Long> getParticipantIds() {
    return Collections.unmodifiableSet(values.keySet());
  }
  
  public Object getValue(String key, Long participantId) {
     switch (getDataType(key)) {
       case NUMBER:
         return getDouble(key, participantId);
       default:
         return getText(key, participantId);
     }
  } 
  
  public String getText(String key, Long participantId) {
    return getParticipantValues(participantId).get(key);
  }
  
  public Double getDouble(String key, Long participantId) {
    return NumberUtils.createDouble(getText(key, participantId));
  }
  
  public DataType getDataType(String key) {
    return dataTypes.get(key);
  }
  
  public void setValue(String key, Long participantId, String value) {
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
    
    getParticipantValues(participantId).put(key, value);
  }
  
  private Map<String, String> getParticipantValues(Long participantId) {
    if (!values.containsKey(participantId)) {
      this.values.put(participantId, new HashMap<String, String>());
    }
    
    return this.values.get(participantId);
  }
  
  private Set<String> keys;
  private Map<Long, Map<String, String>> values;
  private Map<String, DataType> dataTypes;
  
  public enum DataType {
    TEXT,
    NUMBER
  }
}

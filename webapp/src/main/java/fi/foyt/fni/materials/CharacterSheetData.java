package fi.foyt.fni.materials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.model.materials.CharacterSheetEntryType;

public class CharacterSheetData {
  
  public CharacterSheetData(CharacterSheetMeta meta) {
    this.meta = meta;
    this.data = new HashMap<Long, Map<String,String>>();
  }

  public Set<Long> getUserIds() {
    return Collections.unmodifiableSet(data.keySet());
  }

  public Set<String> getEntryNames() {
    Set<String> entryNames = new HashSet<>();
    
    for (Long userId : getUserIds()) {
      for (String entryName : data.get(userId).keySet()) {
        if (!entryNames.contains(entryName)) {
          entryNames.add(entryName);
        }
      }
    }
    
    return entryNames;
  }
  
  public List<String> getSortedEntryNames() {
    List<String> result = new ArrayList<>(getEntryNames());
    Collections.sort(result);
    return result;
  }
  
  public CharacterSheetMetaField getFieldMeta(String name) {
    return meta.get(name);
  }
  
  public Object getValue(String key, Long userId) {
    switch (getDataType(key)) {
      case NUMBER:
        return getDouble(key, userId);
      default:
        return getText(key, userId);
    }
  } 

  public CharacterSheetEntryType getDataType(String key) {
    return getFieldMeta(key).getType();
  }
  
  public String getText(String key, Long userId) {
    return getUserValues(userId).get(key);
  }
 
  public Double getDouble(String key, Long userId) {
    String text = getText(key, userId);
    if (StringUtils.isNotBlank(text)) {
      return NumberUtils.createDouble(text);
    }
    
    return null;
  }
  
  public void setValue(String key, Long userId, String value) {
    getUserValues(userId).put(key, value);
  }
  
  private Map<String, String> getUserValues(Long userId) {
    if (!data.containsKey(userId)) {
      this.data.put(userId, new HashMap<String, String>());
    }
    
    return this.data.get(userId);
  }
  
  private Map<Long, Map<String, String>> data;
  private CharacterSheetMeta meta;
}

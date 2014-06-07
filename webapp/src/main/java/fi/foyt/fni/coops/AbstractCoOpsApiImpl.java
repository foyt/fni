package fi.foyt.fni.coops;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.coops.CoOpsApi;
import fi.foyt.fni.persistence.model.materials.MaterialSetting;

public abstract class AbstractCoOpsApiImpl implements CoOpsApi {

  protected final static String COOPS_PROTOCOL_VERSION = "1.0.0";

  protected Map<String, String> settingToProperties(String prefix, List<MaterialSetting> settings) {
    Map<String, String> properties = new HashMap<>();
    for (MaterialSetting setting : settings) {
      String key = StringUtils.removeStart(setting.getKey().getName(), prefix);
      properties.put(key, setting.getValue());
    }
    
    return properties;
  }
  
}

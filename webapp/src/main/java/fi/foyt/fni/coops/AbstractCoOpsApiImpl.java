package fi.foyt.fni.coops;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.coops.CoOpsAlgorithm;
import fi.foyt.coops.CoOpsApi;
import fi.foyt.fni.persistence.model.materials.MaterialSetting;

public abstract class AbstractCoOpsApiImpl implements CoOpsApi {

  protected final static String COOPS_PROTOCOL_VERSION = "1.0.0";

  protected void setAlgorithms(Map<String, CoOpsAlgorithm> algorithms) {
    this.algorithms = algorithms;
  }
  
  protected CoOpsAlgorithm getAlgorithm(String algorithm) {
    return algorithms.get(algorithm);
  }
  
  protected Set<String> getSupportedAlgorithmNames() {
    return algorithms.keySet();
  }

  protected String chooseAlgorithm(List<String> clientAlgorithms) {
    for (String clientAlgorithm : clientAlgorithms) {
      if (algorithms.containsKey(clientAlgorithm)) {
        return clientAlgorithm;
      }
    }
    
    return null;
  }

  protected Map<String, String> settingToProperties(String prefix, List<MaterialSetting> settings) {
    Map<String, String> properties = new HashMap<>();
    for (MaterialSetting setting : settings) {
      String key = StringUtils.removeStart(setting.getKey().getName(), prefix);
      properties.put(key, setting.getValue());
    }
    
    return properties;
  }
  
  private Map<String, CoOpsAlgorithm> algorithms;
}

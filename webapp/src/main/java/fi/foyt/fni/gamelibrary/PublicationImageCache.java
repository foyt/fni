package fi.foyt.fni.gamelibrary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.utils.data.TypedData;

@ApplicationScoped
public class PublicationImageCache {
  
  @PostConstruct
  public void init() {
    cache = new HashMap<>();
  }
  
  public void put(Long id, Integer width, Integer height, TypedData imageData) {
    String cacheKey = createCacheKey(id, width, height);
    getCache().put(cacheKey, imageData);
  }
  
  public TypedData get(Long id, Integer width, Integer height) {
    String cacheKey = createCacheKey(id, width, height);
    return getCache().get(cacheKey);
  }

  public void remove(Long id) {
    String prefix = id.toString();
    Set<String> clearKeys = new HashSet<>();
    Map<String,TypedData> cache = getCache();
    
    for (String key : cache.keySet()) {
      if (StringUtils.startsWith(key, prefix)) {
        clearKeys.add(key);
      }
    }
    
    for (String clearKey : clearKeys) {
      cache.remove(clearKey);
    }
  }
  
  private Map<String, TypedData> getCache() {
    return cache;
  }
  
  private String createCacheKey(Long id, Integer width, Integer height) {
    return new StringBuilder()
      .append(id)
      .append('-')
      .append(width)
      .append('-')
      .append(height)
      .toString();
  }

  private Map<String, TypedData> cache;

}

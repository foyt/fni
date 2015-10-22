package fi.foyt.fni.debug;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class DebugTimerCollector {
  
  @PostConstruct
  public void init() {
    calls = new HashMap<Method, List<Long>>();
  }
  
  public void addCall(Method method, long time) {
    if (calls.containsKey(method)) {
      calls.get(method).add(time);
    } else {
      calls.put(method, new ArrayList<>(Arrays.asList(time)));
    }
  }
  
  public Map<Method, List<Long>> getCalls() {
    return Collections.unmodifiableMap(calls);
  }

  private Map<Method, List<Long>> calls;
}

package fi.foyt.fni.debug;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class DebugTimerResults implements Serializable {

  private static final long serialVersionUID = 7694312148885572882L;

  @PostConstruct
  public void init() {
    requestStats = new ArrayList<>();
  }
  
  public void reset() {
    requestStats = new ArrayList<>();
  }

  public List<RequestStats> getRequestStats() {
    return requestStats;
  }

  public void setRequestStats(List<RequestStats> requestStats) {
    this.requestStats = requestStats;
  }
  
  public void addRequestStats(String view, Map<Method, List<Long>> calls, long requestMills) {
    Set<MethodStats> methodStats = new TreeSet<>(new Comparator<MethodStats>() {
      @Override
      public int compare(MethodStats o1, MethodStats o2) {
        return (int) ((int) o2.getTotal() - o1.getTotal());
      }
    });
    
    for (Entry<Method,List<Long>> entry : calls.entrySet()) {
      Method method = entry.getKey();
      List<Long> times = entry.getValue();

      long avg = 0;
      long total = 0;
      long min = Long.MAX_VALUE;
      long max = Long.MIN_VALUE;

      for (Long time : times) {
        total += time;
        min = Math.min(min, time);
        max = Math.max(max, time);
      }

      avg = total / times.size();
      String methodName = method.getDeclaringClass().getSimpleName() + '.' + method.getName();

      methodStats.add(new MethodStats(methodName, method.toString(), times.size(), avg, min, max, total, total));
    }

    requestStats.add(new RequestStats(view, methodStats, requestMills));
  }

  private List<RequestStats> requestStats;
}

package fi.foyt.fni.debug;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;

@SessionScoped
@Stateful
public class DebugTimerResults {

  @PostConstruct
  public void init() {
    requestStats = new ArrayList<>();
  }

  public List<RequestStats> getRequestStats() {
    return requestStats;
  }

  public void setRequestStats(List<RequestStats> requestStats) {
    this.requestStats = requestStats;
  }
  
  public void addRequestStats(String view, Map<Method, List<Long>> calls, long requestMills) {
    List<MethodStats> methodStats = new ArrayList<>();

    for (Method method : calls.keySet()) {
      List<Long> times = calls.get(method);

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
      
      methodStats.add(new MethodStats(method.getName(), method.toString(), times.size(), avg, min, max, total));
    }
    
    Collections.sort(methodStats, new Comparator<MethodStats>() {
      @Override
      public int compare(MethodStats o1, MethodStats o2) {
        return (int) ((int) o2.getTotal() - o1.getTotal());
      }
    });

    requestStats.add(new RequestStats(view, methodStats, requestMills));
  }

  private List<RequestStats> requestStats;

  public class RequestStats {

    public RequestStats(String view, List<MethodStats> methodStats, long requestMills) {
      super();
      this.view = view;
      this.methodStats = methodStats;
      this.requestMills = requestMills;
    }

    public String getView() {
      return view;
    }

    public void setView(String view) {
      this.view = view;
    }

    public List<MethodStats> getMethodStats() {
      return methodStats;
    }

    public void setMethodStats(List<MethodStats> methodStats) {
      this.methodStats = methodStats;
    }
    
    public long getRequestMills() {
      return requestMills;
    }

    private String view;
    private List<MethodStats> methodStats;
    private long requestMills;
  }

  public class MethodStats {

    public MethodStats(String methodName, String methodDetails, int count, long avg, long min, long max, long total) {
      this.methodDetails = methodDetails;
      this.methodName = methodName;
      this.count = count;
      this.avg = avg;
      this.min = min;
      this.max = max;
      this.total = total;
    }
    
    public String getMethodDetails() {
      return methodDetails;
    }
    
    public void setMethodDetails(String methodDetails) {
      this.methodDetails = methodDetails;
    }
    
    public String getMethodName() {
      return methodName;
    }
    
    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }

    public int getCount() {
      return count;
    }

    public long getAvg() {
      return avg;
    }

    public long getMin() {
      return min;
    }

    public long getMax() {
      return max;
    }

    public long getTotal() {
      return total;
    }

    private String methodName;
    private String methodDetails;
    private int count;
    private long avg;
    private long min;
    private long max;
    private long total;
  }
}

package fi.foyt.fni.debug;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;

import org.apache.commons.lang3.StringUtils;

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
      String methodName = method.getDeclaringClass().getSimpleName() + '.' + method.getName();

      methodStats.add(new MethodStats(methodName, method.toString(), times.size(), avg, min, max, total, total));
    }

    requestStats.add(new RequestStats(view, methodStats, requestMills));
  }

  private List<RequestStats> requestStats;

  public class RequestStats implements Cloneable {

    public RequestStats(String view, Set<MethodStats> methodStats, long requestMills) {
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
    
    public List<MethodStats> getMethodStatList() {
      return new ArrayList<>(getMethodStats());
    }

    public Set<MethodStats> getMethodStats() {
      return methodStats;
    }

    public void setMethodStats(Set<MethodStats> methodStats) {
      this.methodStats = methodStats;
    }
    
    public void addMethodStats(Set<MethodStats> methodStats) {
      for (MethodStats methodStat : methodStats) {
        MethodStats existing = getMethodStat(methodStat.getMethodName());
        if (existing == null) {
          this.methodStats.add(methodStat);
        } else {
          existing.merge(methodStat);
        }
      }
    }
    
    private MethodStats getMethodStat(String methodName) {
      for (MethodStats methodStat : this.methodStats) {
        if (StringUtils.equals(methodName, methodStat.getMethodName())) {
          return methodStat;
        }
      }
      
      return null;
    }

    public long getRequestMills() {
      return requestMills;
    }
    
    public void setRequestMills(long requestMills) {
      this.requestMills = requestMills;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
      Set<MethodStats> methodStats = new TreeSet<>(new Comparator<MethodStats>() {
        @Override
        public int compare(MethodStats o1, MethodStats o2) {
          return (int) ((int) o2.getTotal() - o1.getTotal());
        }
      });
      
      for (MethodStats methodStat : this.methodStats) {
        methodStats.add((MethodStats) methodStat.clone());
      }
      
      return new RequestStats(this.view, methodStats, this.requestMills);
    }

    private String view;
    private Set<MethodStats> methodStats;
    private long requestMills;
  }

  public class MethodStats implements Cloneable {

    public MethodStats(String methodName, String methodDetails, int count, long avg, long min, long max, long total, long totalAvg) {
      this.methodDetails = methodDetails;
      this.methodName = methodName;
      this.count = count;
      this.avg = avg;
      this.min = min;
      this.max = max;
      this.total = total;
      this.totalAvg = totalAvg;
    }
    
    public void merge(MethodStats methodStat) {
      this.count += methodStat.getCount();
      this.avg = (this.avg + methodStat.getAvg()) / 2;
      this.min = Math.min(this.min, methodStat.getMin());
      this.max = Math.min(this.max, methodStat.getMax());
      this.totalAvg = (this.totalAvg + methodStat.getTotalAvg()) / 2;
      this.total += methodStat.getTotal();
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
    
    public long getTotalAvg() {
      return totalAvg;
    }

    public long getTotal() {
      return total;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
      return new MethodStats(methodName, methodDetails, count, avg, min, max, total, totalAvg);
    }

    private String methodName;
    private String methodDetails;
    private int count;
    private long avg;
    private long min;
    private long max;
    private long totalAvg;
    private long total;
  }
}

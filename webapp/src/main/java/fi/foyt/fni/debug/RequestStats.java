package fi.foyt.fni.debug;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

public class RequestStats implements Serializable {

  private static final long serialVersionUID = -7236497692162409791L;
  
  public RequestStats(String view, Set<MethodStats> methodStats, long requestMills) {
    super();
    this.view = view;
    this.methodStats = methodStats;
    this.requestMills = requestMills;
  }
  
  public RequestStats(RequestStats source) {
    Set<MethodStats> clonedMethodStats = new TreeSet<>(new Comparator<MethodStats>() {
      @Override
      public int compare(MethodStats o1, MethodStats o2) {
        return (int) ((int) o2.getTotal() - o1.getTotal());
      }
    });
    
    for (MethodStats methodStat : source.getMethodStats()) {
      clonedMethodStats.add(new MethodStats(methodStat));
    }
    
    this.view = source.getView();
    this.methodStats = clonedMethodStats;
    this.requestMills = source.getRequestMills();
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

  private String view;
  private Set<MethodStats> methodStats;
  private long requestMills;
}
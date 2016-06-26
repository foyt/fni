package fi.foyt.fni.debug;

import java.io.Serializable;

public class MethodStats implements Serializable {

  private static final long serialVersionUID = -5142279657674657714L;
  
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
  
  public MethodStats(MethodStats source) {
    this.methodDetails = source.getMethodDetails();
    this.methodName = source.getMethodName();
    this.count = source.getCount();
    this.avg = source.getAvg();
    this.min = source.getMin();
    this.max = source.getMax();
    this.total = source.getTotal();
    this.totalAvg = source.getTotalAvg();
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
  
  private String methodName;
  private String methodDetails;
  private int count;
  private long avg;
  private long min;
  private long max;
  private long totalAvg;
  private long total;
}
package fi.foyt.fni.view.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.debug.DebugTimerResults;
import fi.foyt.fni.debug.DebugTimerResults.RequestStats;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Stateful
@Named
@Join (path = "/admin/debug-timer-results", to = "/admin/debug-timer-results.jsf")
@LoggedIn
@Secure (Permission.SYSTEM_ADMINISTRATION)
public class DebugTimerResultsBackingBean {
  
  @Parameter
  private Boolean reset;
  
  @Inject
  private Logger logger;
  
  @Inject
  private DebugTimerResults debugTimerResults;
  
  @RequestAction
  public String load() {
    if (Boolean.TRUE.equals(reset)) {
      debugTimerResults.reset();
      return "/admin/debug-timer-results.jsf?faces-redirect=true";
    } else {
      List<RequestStats> originalRequestStats = new ArrayList<>(debugTimerResults.getRequestStats());
      originalRequestStats = removeResourceRequestStats(originalRequestStats);
      
      requestStats = new ArrayList<>(mergeRequestStats(originalRequestStats));
      
      Collections.sort(requestStats, new Comparator<RequestStats>() {
        @Override
        public int compare(RequestStats o1, RequestStats o2) {
          return (int) (o2.getRequestMills() - o1.getRequestMills());
        }
      });
      
      return null;
    }
  }

  private List<RequestStats> removeResourceRequestStats(List<RequestStats> requestStats) {
    List<RequestStats> result = new ArrayList<>();
    
    try {
      for (RequestStats requestStat : requestStats) {
        if (!StringUtils.contains(requestStat.getView(), "debug-timer-results.jsf") && 
            !StringUtils.contains(requestStat.getView(), "javax.faces.resource")) {
          result.add(requestStat);
        };
      }
    } catch (Exception e) {
      logger.log(Level.SEVERE, "Failed to remove resource requests", e);
    }
    
    return result;
  }

  public List<RequestStats> getRequestStats() {
    return requestStats;
  }
  
  public Boolean getReset() {
    return reset;
  }
  
  public void setReset(Boolean reset) {
    this.reset = reset;
  }
  
  private List<RequestStats> mergeRequestStats(List<RequestStats> stats) {
    Map<String, RequestStats> map = new HashMap<>();
    
    for (RequestStats stat : stats) {
      try {
        if (!map.containsKey(stat.getView())) {
          try {
            map.put(stat.getView(), (RequestStats) stat.clone());
          } catch (CloneNotSupportedException e) {
          } 
        } else {
          RequestStats existing = map.get(stat.getView());
          existing.setRequestMills((existing.getRequestMills() + stat.getRequestMills()) / 2);  
          existing.addMethodStats(stat.getMethodStats());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    return new ArrayList<>(map.values());
  }

  public String getHumanReadableDuration(long mills) {
	  return DurationFormatUtils.formatDuration(mills, "s's' S'ms'", false);
	}
  
  private List<RequestStats> requestStats;
}

package fi.foyt.fni.view.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.debug.DebugTimerResults;
import fi.foyt.fni.debug.DebugTimerResults.RequestStats;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Named
@Join (path = "/admin/debug-timer-results", to = "/admin/debug-timer-results.jsf")
@LoggedIn
@Secure (Permission.SYSTEM_ADMINISTRATION)
public class DebugTimerResultsBackingBean {
  
  @Inject
  private DebugTimerResults debugTimerResults;
  
  @RequestAction
  public void load() {
    
  }

  public List<RequestStats> getRequestStats() {
    List<RequestStats> result = new ArrayList<>(debugTimerResults.getRequestStats());
    
    Collections.sort(result, new Comparator<RequestStats>() {
      @Override
      public int compare(RequestStats o1, RequestStats o2) {
        return (int) (o2.getRequestMills() - o1.getRequestMills());
      }
    });
    
    return result;
  }

  public String getHumanReadableDuration(long mills) {
	  return DurationFormatUtils.formatDuration(mills, "s's' S'ms'", false);
	}
  
}

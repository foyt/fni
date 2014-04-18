package fi.foyt.fni.view.admin;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.debug.DebugTimerResults;
import fi.foyt.fni.debug.DebugTimerResults.RequestStats;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = { 
  @URLMapping(
	  id = "admin-debug-timer-results", 
		pattern = "/admin/debug-timer-results/", 
		viewId = "/admin/debug-timer-results.jsf"
  )
})
public class DebugTimerResultsBackingBean {
  
  @Inject
  private DebugTimerResults debugTimerResults;
  
//	@URLAction
//	@LoggedIn
//	@Secure (Permission.SYSTEM_ADMINISTRATION)
	public void load() throws GeneralSecurityException, IOException {
	  System.out.println("URL Action!");
	}

//	@LoggedIn
//  @Secure (Permission.SYSTEM_ADMINISTRATION)
	public List<RequestStats> getRequestStats() {
    return debugTimerResults.getRequestStats();
  }
	
	public String getHumanReadableDuration(long mills) {
	  return DurationFormatUtils.formatDuration(mills, "s's' S'ms'", false);
	}
  
}

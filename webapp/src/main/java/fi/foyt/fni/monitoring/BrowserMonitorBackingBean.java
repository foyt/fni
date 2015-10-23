package fi.foyt.fni.monitoring;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

@RequestScoped
@Named
@Stateful
public class BrowserMonitorBackingBean {

  @Inject
  private BrowserMonitorController browserMonitorController;
  
  @PostConstruct
  public void init() {
    script = browserMonitorController.getMonitoringScript();
    enabled = StringUtils.isNotBlank(script);
  }
  
  public boolean getEnabled() {
    return enabled;
  }
  
  public String getScript() {
    return script;
  }
  
  private boolean enabled;
  private String script;
}

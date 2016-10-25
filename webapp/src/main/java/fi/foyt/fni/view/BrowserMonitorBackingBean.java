package fi.foyt.fni.view;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.monitoring.BrowserMonitorController;

@RequestScoped
@Named
@Stateful
public class BrowserMonitorBackingBean {

  @Inject
  private BrowserMonitorController browserMonitorController;
  
  @PostConstruct
  @TransactionAttribute (TransactionAttributeType.REQUIRES_NEW)
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

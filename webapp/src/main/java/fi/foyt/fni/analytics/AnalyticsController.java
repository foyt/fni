package fi.foyt.fni.analytics;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.system.SystemSettingsController;

@Named
@RequestScoped
@Stateful
public class AnalyticsController {
	
	@Inject
	private SystemSettingsController systemSettingsController;

  public String getWebPropertyId() {
    return webPropertyId;
  }

  @PostConstruct
  private void init() {
    webPropertyId = systemSettingsController.getSetting("analytics.webPropertyId");
  }
  
  private String webPropertyId;
}

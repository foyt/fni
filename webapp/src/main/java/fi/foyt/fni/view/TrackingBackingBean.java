package fi.foyt.fni.view;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
@Named
@Stateful
public class TrackingBackingBean {

	@Inject
	private SystemSettingsController systemSettingsController;
	
	@PostConstruct
	public void init() {
	  piwikUrl = systemSettingsController.getSetting(SystemSettingKey.PIWIK_URL);
	  piwikSiteId = systemSettingsController.getSetting(SystemSettingKey.PIWIK_SITEID); 
	  piwikEnabled = StringUtils.isNotBlank(piwikSiteId) && StringUtils.isNotBlank(piwikSiteId);
	  googleAnalyticsId = systemSettingsController.getSetting(SystemSettingKey.GOOGLE_ANALYTICS_TRACKING_ID); 
	  googleAnalyticsEnabled = StringUtils.isNotBlank(googleAnalyticsId);
	}

	public boolean getPiwikEnabled() {
	  return piwikEnabled;
	}
	
	public String getPiwikUrl() {
    return piwikUrl;
	}
	
	public String getPiwikSiteId() {
    return piwikSiteId;
	}
	
	public boolean getGoogleAnalyticsEnabled() {
	  return googleAnalyticsEnabled;
	}
	
	public String getGoogleAnalyticsId() {
    return googleAnalyticsId;
  }

  private boolean piwikEnabled;
	private String piwikUrl;
	private String piwikSiteId;
	private boolean googleAnalyticsEnabled;
	private String googleAnalyticsId;
}

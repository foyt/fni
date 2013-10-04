package fi.foyt.fni.tracking;

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
public class PiwikBackingBean {

	@Inject
	private SystemSettingsController systemSettingsController;
	
	public String getPiwikUrl() {
		return systemSettingsController.getSetting(SystemSettingKey.PIWIK_URL);
	}
	
	public boolean getHasPiwikUrl() {
		return StringUtils.isNotBlank(getPiwikUrl());
	}
	
}

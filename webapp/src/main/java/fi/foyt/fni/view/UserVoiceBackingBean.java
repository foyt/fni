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
public class UserVoiceBackingBean {

	@Inject
	private SystemSettingsController systemSettingsController;
	
	@PostConstruct
	public void init() {
		clientKey = systemSettingsController.getSetting(SystemSettingKey.USERVOICE_CLIENT_KEY);
	}
	
	public String getClientKey() {
		return clientKey;
	}

	public boolean getHasUserVoice() {
		return StringUtils.isNotBlank(getClientKey());
	}

	private String clientKey;
}

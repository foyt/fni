package fi.foyt.fni.view;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.system.SystemSettingsController;

@Named
@RequestScoped
@Stateful
public class MenuBackingBean {

	@Inject
	private SystemSettingsController systemSettingsController;
	
	public List<Language> getLocalizedLanguages() {
		return systemSettingsController.listLocalizedLanguages();
	}
	
}

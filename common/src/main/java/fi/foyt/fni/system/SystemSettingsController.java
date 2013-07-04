package fi.foyt.fni.system;

import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.system.SystemSettingDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.system.SystemSetting;

@RequestScoped
@Stateful
public class SystemSettingsController {

	@Inject
	@DAO
	private SystemSettingDAO systemSettingDAO;
	
	@Inject
	@DAO
	private LanguageDAO languageDAO;

	public String getSetting(String name) {
		SystemSetting systemSetting = systemSettingDAO.findByName(name);
		if (systemSetting != null)
			return systemSetting.getValue();

		return null;
	}

	public Integer getIntegerSetting(String name) {
		return NumberUtils.createInteger(getSetting(name));
	}

	public Locale getLocaleSetting(String name) {
		String setting = getSetting(name);
		return LocaleUtils.toLocale(setting);
	}

	public Locale getDefaultLocale() {
		return getLocaleSetting("system.defaultLocale");
	}

	public void updateSetting(String name, String value) {
		SystemSetting systemSetting = systemSettingDAO.findByName(name);
		if (systemSetting == null)
			systemSetting = systemSettingDAO.create(name, value);
		else
			systemSetting = systemSettingDAO.updateValue(systemSetting, value);
	}

	public String getTheme() {
		return "default_dev";
	}

	public String getThemePath(HttpServletRequest request) {
		return request.getContextPath() + "/themes/" + getTheme();
	}
	
	public List<Language> listLanguages() {
		return languageDAO.listAll();
	}
	
	public List<Language> listLocalizedLanguages() {
		return languageDAO.listByLocalized(Boolean.TRUE);
	}
}

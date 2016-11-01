package fi.foyt.fni.system;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.dao.common.CountryDAO;
import fi.foyt.fni.persistence.dao.common.LanguageDAO;
import fi.foyt.fni.persistence.dao.system.SystemSettingDAO;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.system.SystemSetting;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;

@Dependent
public class SystemSettingsController {
	
	private static final String DEFAULT_COUNTRY_CODE = "FI";
  private final static double VAT_PERCENT = 0;
  private final static boolean VAT_REGISTERED = false;
  private static final String DEFAULT_CURRENCY = "EUR";

	@Inject
	private SystemSettingDAO systemSettingDAO;

	@Inject
	private LanguageDAO languageDAO;

	@Inject
	private CountryDAO countryDAO;

	public String getSetting(SystemSettingKey key) {
		SystemSetting systemSetting = systemSettingDAO.findByKey(key);
		if (systemSetting != null)
			return systemSetting.getValue();

		return null;
	}

	public Integer getIntegerSetting(SystemSettingKey key) {
		return NumberUtils.createInteger(getSetting(key));
	}

	public Long getLongSetting(SystemSettingKey key) {
		return NumberUtils.createLong(getSetting(key));
	}

	public Locale getLocaleSetting(SystemSettingKey key) {
		String setting = getSetting(key);
		return LocaleUtils.toLocale(setting);
	}

	public Locale getDefaultLocale() {
		return getLocaleSetting(SystemSettingKey.DEFAULT_LOCALE);
	}

	public void updateSetting(SystemSettingKey key, String value) {
		SystemSetting systemSetting = systemSettingDAO.findByKey(key);
		if (systemSetting == null)
			systemSetting = systemSettingDAO.create(key, value);
		else
			systemSetting = systemSettingDAO.updateValue(systemSetting, value);
	}

	public String getTheme() {
		return "default_dev";
	}

	public String getThemePath(HttpServletRequest request) {
		return request.getContextPath() + "/themes/" + getTheme();
	}

  public Language findLanguageById(Long languageId) {
    return languageDAO.findById(languageId);
  }
	
	public List<Language> listLanguages() {
		return languageDAO.listAll();
	}
	
  public Language getDefaultLanguage() {
    return languageDAO.findByIso2(getSetting(SystemSettingKey.DEFAULT_LANGUAGE));
  }
	
	public List<Language> listLocalizedLanguages() {
		return languageDAO.listByLocalized(Boolean.TRUE);
	}
	
	public boolean isSupportedLocale(String locale) {
	  return isSupportedLocale(LocaleUtils.toLocale(locale));
	}
	
	public boolean isSupportedLocale(Locale locale) {
    for (Language language : listLocalizedLanguages()) {
      if (locale.getLanguage().equals(LocaleUtils.toLocale(language.getISO2()).getLanguage())) {
        return true;
      }
    }
    
    return false;
  }

	public Country findCountryById(Long id) {
		return countryDAO.findById(id);
	}

	public Country findCountryByCode(String code) {
		return countryDAO.findByCode(code);
	}

	public Country getDefaultCountry() {
		return findCountryByCode(DEFAULT_COUNTRY_CODE);
	}
	
	public List<Country> listCountries() {
		return countryDAO.listAll();
	}

	public Language findLocaleByIso2(String iso2) {
		return languageDAO.findByIso2(iso2);
	}

	public double getVatPercent() {
    return VAT_PERCENT;
  }
	
	public boolean isVatRegistered() {
    return VAT_REGISTERED;
  }

  public Currency getDefaultCurrency() {
    return Currency.getInstance(DEFAULT_CURRENCY);
  }

  public Double getDoubleSetting(SystemSettingKey key) {
    return NumberUtils.createDouble(getSetting(key));
  }

  public Currency getCurrencySetting(SystemSettingKey key) {
    return Currency.getInstance(getSetting(key));
  }
  
  public String getSiteHost() {
    return System.getProperty("fni-host");
  }

  public Integer getSiteHttpPort() {
    return NumberUtils.createInteger(System.getProperty("fni-http-port"));
  }
  
  public Integer getSiteHttpsPort() {
    return NumberUtils.createInteger(System.getProperty("fni-https-port"));
  }
  
  public String getSiteContextPath() {
    return System.getProperty("fni-context-path");
  }
  
  public String getSiteUrl(boolean secure, boolean includeContextPath) {
    return getHostUrl(getSiteHost(), secure, includeContextPath);
  }
  
  public String getHostUrl(String host, boolean secure, boolean includeContextPath) {
    boolean useSecure = secure && !getTestMode(); 
    
    int port = useSecure ? getSiteHttpsPort() : getSiteHttpPort();
    String scheme = useSecure ? "https" : "http";
    boolean dropPort = useSecure ? port == 443 : port == 80;
    
    StringBuilder resultBuilder = new StringBuilder();
    
    resultBuilder.append(scheme);
    resultBuilder.append("://");
    resultBuilder.append(host);
    
    if (!dropPort) {
      resultBuilder.append(':');
      resultBuilder.append(port);
    }
    
    if (includeContextPath) {
      resultBuilder.append(getSiteContextPath());
    }
    
    return resultBuilder.toString();
  }
  
  public boolean getTestMode() {
    return StringUtils.equals(System.getProperty("it-test"), "true");
  }
}

package fi.foyt.fni.i18n;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

public class ExternalLocales {
  
  private static final Logger logger = Logger.getLogger(ExternalLocales.class.getName());

  public static String getText(Locale locale, String key) {
    return getResourceBundle(locale).getString(key);
  }
  
  public static String getText(Locale locale, String key, Object... params) {
    return (new MessageFormat(getText(locale, key), locale)).format(params);
  }
  
  public static String getText(Locale locale, String key, Collection<?> params) {
    return getText(locale, key, params.toArray());
  }

  public static String getUnformatted(Locale locale, String key) {
    return getResourceBundle(locale).getString(key);
  }
  
  private static ResourceBundle getResourceBundle(Locale locale) {
    if (!bundles.containsKey(locale)) {
      bundles.put(locale, loadBundle(locale));
    }
    
    return bundles.get(locale);
  }
  
  private static ResourceBundle loadBundle(Locale locale) {
  	try {
  	  ResourceBundle bundle = ResourceBundle.getBundle("fi.foyt.fni.external.locales", locale);
    	return bundle;
	  } catch (Exception e) {
	    logger.log(Level.WARNING, "Failed to load resource bundle", e);
	    
	    if (StringUtils.isNotBlank(locale.getCountry())) { 
  			return loadBundle(LocaleUtils.toLocale(locale.getLanguage()));
  		} else {
  			return ResourceBundle.getBundle("fi.foyt.fni.external.locales");
  		}
  	}
  }
  
  private static Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
}

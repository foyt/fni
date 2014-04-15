package fi.foyt.fni.i18n;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

public class ExternalLocales {

  public static String getText(Locale locale, String key) {
    return getResourceBundle(locale).getString(key);
  }
  
  public static String getText(Locale locale, String key, Object... params) {
    return MessageFormat.format(getText(locale, key), params);
  }
  
  public static String getText(Locale locale, String key, Collection<?> params) {
    return getText(locale, key, params.toArray());
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
  		if (StringUtils.isNotBlank(locale.getCountry())) { 
  			return loadBundle(LocaleUtils.toLocale(locale.getLanguage()));
  		} else {
  			return ResourceBundle.getBundle("fi.foyt.fni.external.locales");
  		}
  	}
  }
  
  private static Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
}

package fi.foyt.fni.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

public class JavaScriptMessages {
  
  private static final Logger logger = Logger.getLogger(JavaScriptMessages.class.getName());

  public static String getText(Locale locale, String key) {
    return getResourceBundle(locale).getString(key);
  }
  
  public static String getText(Locale locale, String key, Object[] params) {
    return MessageFormat.format(getText(locale, key), params);
  }
  
  public static ResourceBundle getResourceBundle(Locale locale) {
    if (!bundles.containsKey(locale)) {
      bundles.put(locale, loadBundle(locale));
    }

    return bundles.get(locale);
  }
  
  private static ResourceBundle loadBundle(Locale locale) {
  	try {
  	  ResourceBundle bundle = ResourceBundle.getBundle("fi.foyt.fni.i18n.JavaScriptMessages", locale);
    	return bundle;
	  } catch (Exception e) {
      logger.log(Level.WARNING, "Could not load resource bundle", e);
	    
  		if (StringUtils.isNotBlank(locale.getCountry())) { 
  			return loadBundle(new Locale(locale.getLanguage()));
  		} else {
  			return ResourceBundle.getBundle("fi.foyt.fni.i18n.JavaScriptMessages");
  		}
  	}
  }
  
  private static Map<Locale, ResourceBundle> bundles = new HashMap<Locale, ResourceBundle>();
}


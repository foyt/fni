package fi.foyt.fni.view.generic;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;

import org.json.JSONException;
import org.json.JSONObject;

import fi.foyt.fni.i18n.JavaScriptMessages;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Stateful
public class JavaScriptLocalesViewController extends AbstractViewController {

	@Override
	public boolean checkPermissions(ViewControllerContext context) {
		return true;
	}

	@Override
	public void execute(ViewControllerContext context) {
		Map<String, String> localeStrings = new HashMap<String, String>();
    
    String localeParam = context.getStringParameter("locale");
    String[] localeSplit = localeParam.split("_");
    
    Locale locale = null;
    if (localeSplit.length == 1) {
    	locale = new Locale(localeParam);
    } else {
    	locale = new Locale(localeSplit[0], localeSplit[1]);
    }
    
    ResourceBundle resourceBundle = JavaScriptMessages.getResourceBundle(locale);
    Enumeration<String> keys = resourceBundle.getKeys();
    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      String value = resourceBundle.getString(key);
      localeStrings.put(key, value.trim());
    }

    Map<String, String> settingStrings = new HashMap<String, String>();
    DateFormat shortDate = SimpleDateFormat.getDateInstance(DateFormat.SHORT, locale);
    DateFormat longDate = SimpleDateFormat.getDateInstance(DateFormat.LONG, locale);
    DateFormat time = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, locale);

    if (shortDate instanceof SimpleDateFormat)
      settingStrings.put("dateFormatShort", ((SimpleDateFormat) shortDate).toPattern());
    if (longDate instanceof SimpleDateFormat)
      settingStrings.put("dateFormatLong", ((SimpleDateFormat) longDate).toPattern());
    if (time instanceof SimpleDateFormat)
      settingStrings.put("timeFormat", ((SimpleDateFormat) time).toPattern());

    try {
      JSONObject result = new JSONObject();
      result.put("localeStrings", localeStrings);
      result.put("settings", settingStrings);
      
      context.setData(new TypedData(result.toString(1).getBytes("UTF-8"), "application/json"));
    } catch (JSONException e) {
    	throw new ViewControllerException(e.getMessage());
    } catch (UnsupportedEncodingException e) {
    	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "error.generic.configurationError"));
    }
	}
	
}
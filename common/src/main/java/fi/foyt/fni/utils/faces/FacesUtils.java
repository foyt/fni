package fi.foyt.fni.utils.faces;

import java.text.MessageFormat;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class FacesUtils {

	public static String getLocalizedValue(String key, Object... params) {
		return MessageFormat.format(getLocalizedValue(key), params);
	}

	public static String getLocalizedValue(String key) {
	  return FacesContext.getCurrentInstance()
	  		.getApplication()
	  		.getResourceBundle(FacesContext.getCurrentInstance(), "locales")
	  		.getString(key);
	}
	
	public static void addMessage(Severity severity, String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, message, null));
	}
}

package fi.foyt.fni.utils.faces;

import java.text.MessageFormat;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
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

	public static String getLocalAddress(boolean includeContextPath) {
		 ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		 
		 int port = externalContext.getRequestServerPort();
		 String serverName = externalContext.getRequestServerName();
		 String scheme = externalContext.getRequestScheme();
		 boolean dropPort = ((port == 80) && "http".equals(scheme)) || ((port == 443) && "https".equals(scheme));
		
		 StringBuilder resultBuilder = new StringBuilder();
		 
		 resultBuilder.append(scheme);
		 resultBuilder.append("://");
		 resultBuilder.append(serverName);
		 
		 if (!dropPort) {
			 resultBuilder.append(':');
			 resultBuilder.append(port);
		 }
		 
		 if (includeContextPath) {
			 resultBuilder.append(externalContext.getRequestContextPath());
		 }
		 
		 return resultBuilder.toString();
	}
}

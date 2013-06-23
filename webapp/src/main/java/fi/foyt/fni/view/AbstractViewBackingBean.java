package fi.foyt.fni.view;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class AbstractViewBackingBean {

	protected ResourceBundle getLocales(FacesContext facesContext) {
		Application application = facesContext.getApplication();
	  return application.getResourceBundle(facesContext, "locales");
	}

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	
	protected ExternalContext getExternalContext(FacesContext facesContext) {
		ExternalContext externalContext = facesContext.getExternalContext();
		return externalContext;
	}
	
	protected ExternalContext getExternalContext() {
		FacesContext facesContext = getFacesContext();
		return getExternalContext(facesContext);
	}
	
	protected String getBasePath(ExternalContext externalContext) {
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    String requestURL = request.getRequestURL().toString();
    String requestURI = request.getRequestURI();
    String contextPath = request.getContextPath();
    String result = requestURL.substring(0, requestURL.length() - requestURI.length()) + contextPath; 
    return result;
  }
	
	protected String getRequestParameter(ExternalContext externalContext, String name) {
		return externalContext.getRequestParameterMap().get(name);
	}

	protected String[] getRequestParameters(ExternalContext externalContext, String name) {
		return externalContext.getRequestParameterValuesMap().get(name);
	}

	protected String getLocalizedValue(ResourceBundle locales, String key) {
		return locales.getString(key);
	}
	
	protected String getLocalizedValue(ResourceBundle locales, String key, Object... params) {
		return MessageFormat.format(getLocalizedValue(locales, key), params);
	}
	
	protected void addNotification(NotificationSeverity severity, String message) {
		@SuppressWarnings("unchecked")
		List<Notification> notifications = (List<Notification>) getRequestAttriute("notifications");
		if (notifications == null) {
			notifications = new ArrayList<>();
			setRequestAttriute("notifications", notifications);
		}
		
		notifications.add(new Notification(severity, message));
	}
	
	protected void addJavaScriptAction(String action, Map<String, String> parameters) {
		@SuppressWarnings("unchecked")
		List<JavaScriptAction> actions = (List<JavaScriptAction>) getRequestAttriute("actions");
		if (actions == null) {
			actions = new ArrayList<>();
			setRequestAttriute("actions", actions);
		}
		
		actions.add(new JavaScriptAction(action, parameters));
	}
	
	protected Object getRequestAttriute(String name) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request =  (HttpServletRequest) externalContext.getRequest();
		return request.getAttribute(name);
	}
	
	protected void setRequestAttriute(String name, Object object) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		HttpServletRequest request =  (HttpServletRequest) externalContext.getRequest();
		request.setAttribute(name, object);
	}
	
	public class JavaScriptAction {
		
		public JavaScriptAction(String action, Map<String, String> parameters) {
			this.action = action;
			this.parameters = parameters;
		}
		
		public String getAction() {
			return action;
		}
		
		public Map<String, String> getParameters() {
			return parameters;
		}
		
		private String action;
		private Map<String, String> parameters;
	}
	
	public class Notification {
		
		public Notification(NotificationSeverity severity, String message) {
			this.message = message;
			this.severity = severity;
		}
		
		public NotificationSeverity getSeverity() {
			return severity;
		}
		
		public String getMessage() {
			return message;
		}
		
		private String message;
		private NotificationSeverity severity;
	}
	
	public enum NotificationSeverity {
		INFO,
		WARNING,
		SERIOUS,
		CRITICAL
	}
	
}

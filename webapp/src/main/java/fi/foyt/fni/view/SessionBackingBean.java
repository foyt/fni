package fi.foyt.fni.view;

import java.io.IOException;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

@Named
@RequestScoped
@Stateful
public class SessionBackingBean {

  @Inject
  private SystemSettingsController systemSettingController;
  
	@Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;

  @Inject
	private HttpServletRequest request;

  @PostConstruct
  public void init() {
    customDomain = !systemSettingController.getSiteHost().equals(request.getServerName());
  }
  
	public boolean isLoggedIn() {
		return sessionController.isLoggedIn();
	}
	
	public User getLoggedUser() {
		return sessionController.getLoggedUser();
	}
	
	public String getLoggedUserEmail() {
		User loggedUser = getLoggedUser();
		if (loggedUser != null) {
			return userController.getUserPrimaryEmail(loggedUser);
		}
		
		return null;
	}
	
	public Locale getLocale() {
		return sessionController.getLocale();
	}
	
	public void changeLocale(String str) throws IOException {
	  Locale locale = LocaleUtils.toLocale(str);
	  sessionController.setLocale(locale);
		
		if (sessionController.isLoggedIn()) {
		  userController.updateUserLocale(sessionController.getLoggedUser(), locale);
		}
	}

	public String getRequestPath() {
	  String forwardRequestUri = (String) request.getAttribute("javax.servlet.forward.request_uri");
	  if (StringUtils.isNotBlank(forwardRequestUri)) {
	    return forwardRequestUri;
	  }

	  return request.getRequestURI();
	}

  public boolean isCustomDomain() {
    return customDomain;
  }
  
  private boolean customDomain;
}

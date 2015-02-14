package fi.foyt.fni.view;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@Named
@RequestScoped
@Stateful
public class SessionBackingBean {
  
  @Inject
  private Logger logger;

  @Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;

  @Inject
	private HttpServletRequest request;
  
  @PostConstruct
  public void init() {
    Map<String, String> dateFormatMap = new HashMap<>();

    dateFormatMap.put("long", getDateFormat(DateFormat.LONG));
    dateFormatMap.put("medium", getDateFormat(DateFormat.MEDIUM));
    dateFormatMap.put("short", getDateFormat(DateFormat.SHORT));
    
    try {
      dateFormats = new ObjectMapper().writeValueAsString(dateFormatMap);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Could not serialize date formatters", e);
    }
    
  }
  
  private String getDateFormat(int style) {
    return ((SimpleDateFormat) DateFormat.getDateInstance(style, getLocale())).toPattern();
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
	
	public String getDateFormats() {
    return dateFormats;
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
	
	private String dateFormats;
}

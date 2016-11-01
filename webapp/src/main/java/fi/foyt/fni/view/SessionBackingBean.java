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
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@Named
@RequestScoped
@Stateful
public class SessionBackingBean {
  
  // From java.text.MessageFormat
  private static final String[] DATE_TIME_MODIFIER_KEYWORDS = {
    "",
    "short",
    "medium",
    "long",
    "full"
  };

  private static final int[] DATE_TIME_MODIFIERS = {
    DateFormat.DEFAULT,
    DateFormat.SHORT,
    DateFormat.MEDIUM,
    DateFormat.LONG,
    DateFormat.FULL,
  };
  
  @Inject
  private Logger logger;

  @Inject
	private SessionController sessionController;

	@Inject
	private UserController userController;

  @Inject
	private HttpServletRequest request;
  
  @PostConstruct
  @TransactionAttribute (TransactionAttributeType.NOT_SUPPORTED)
  public void init() {
    Map<String, String> dateFormats = new HashMap<>();
    Map<String, String> timeFormats = new HashMap<>();

    for (int i = 0, l = DATE_TIME_MODIFIER_KEYWORDS.length; i < l; i++) {
      dateFormats.put(DATE_TIME_MODIFIER_KEYWORDS[i], getDateFormat(DATE_TIME_MODIFIERS[i]));
      timeFormats.put(DATE_TIME_MODIFIER_KEYWORDS[i], getTimeFormat(DATE_TIME_MODIFIERS[i]));
    }

    try {
      this.dateFormats = new ObjectMapper().writeValueAsString(dateFormats);
      this.timeFormats = new ObjectMapper().writeValueAsString(timeFormats);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Could not serialize date formatters", e);
    }
    
    test = StringUtils.equals(System.getProperty("it-test"), "true");
  }
  
  private String getDateFormat(int style) {
    return ((SimpleDateFormat) DateFormat.getDateInstance(style, getLocale())).toPattern();
  }
  
  private String getTimeFormat(int style) {
    return ((SimpleDateFormat) DateFormat.getTimeInstance(style, getLocale())).toPattern();
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
	
	public String getTimeFormats() {
    return timeFormats;
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
	
	public boolean getTest() {
	  return test;
	}
	
	private String dateFormats;
	private String timeFormats;
	private boolean test;
}

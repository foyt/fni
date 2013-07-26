package fi.foyt.fni.session;

import java.io.Serializable;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserToken;

// TODO: Remove Named and references
@Named
@SessionScoped
@Stateful
public class SessionController implements Serializable {
  
	private static final long serialVersionUID = -441183766079031359L;

	@Inject
	@Login
	private Event<UserSessionEvent> loginEvent;
	
	@Inject
	@Logout
	private Event<UserSessionEvent> logoutEvent;

	@Inject
  private UserDAO userDAO;

  @Inject
  private UserTokenDAO userTokenDAO;
  
  public boolean isLoggedIn() {
    return loggedUserId != null;
  }
  
  public UserRole getLoggedUserRole() {
    User loggedUser = getLoggedUser();
    if (loggedUser != null) {
    	return loggedUser.getRole();
    }
    
    return null;
  }
  
  public User getLoggedUser() {
    if (loggedUserId != null)
      return userDAO.findById(loggedUserId);
    else
      return null;
  }
  
  public Long getLoggedUserId() {
    return loggedUserId;
  }
	
  public void login(UserToken token) {
    this.loggedUserId = token.getUserIdentifier().getUser().getId();
    this.loggedUserTokenId = token.getId();
    
    loginEvent.fire(new UserSessionEvent(loggedUserId));
  }

  public UserToken getLoggedUserToken() {
    return userTokenDAO.findById(this.loggedUserTokenId);
  }

  public void logout() {
    // TODO: Delete token...
    
/**
    String accessToken = context.getCookieValue("accessToken");
    if (!StringUtils.isBlank(accessToken)) {
      UserToken token = userTokenDAO.findByToken(accessToken);
      if (token != null)
        userTokenDAO.delete(token);
      
      context.addCookie("accessToken", null, null, "/");
    }
**/    
    Long userId = this.loggedUserId;
  	
    this.loggedUserId = null;
    this.loggedUserTokenId = null;

  	logoutEvent.fire(new UserSessionEvent(userId));
  }

  
  public Locale getLocale() {
    if (locale == null) {
      User user = getLoggedUser();
      
      if (user != null && StringUtils.isNotBlank(user.getLocale()))
        return LocaleUtils.toLocale(user.getLocale());
      
      return Locale.getDefault();
    }
    
    return locale;
  }
  
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
  
  private Long loggedUserId = null;
  private Locale locale = null;
  private Long loggedUserTokenId;
}

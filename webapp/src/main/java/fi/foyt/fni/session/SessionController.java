package fi.foyt.fni.session;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.scribe.model.Token;

import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.security.PermissionController;

@SessionScoped
public class SessionController implements Serializable {
  
	private static final long serialVersionUID = -441183766079031359L;
	
	@Inject
	private Logger logger;

  @Inject
  private UserDAO userDAO;

  @Inject
  private UserTokenDAO userTokenDAO;
  
  @Inject
	private PermissionController permissionController;
  
  @Inject
  private HttpSession session;
	
  @Inject
	@Login
	private Event<UserSessionEvent> loginEvent;
	
	@Inject
	@Logout
	private Event<UserSessionEvent> logoutEvent;
	
	@PostConstruct
	public void init() {
	  loggedUserId = null;
	  
    User user = getLoggedUser();
    
    if (user != null && StringUtils.isNotBlank(user.getLocale())) {
      try {
        locale = LocaleUtils.toLocale(user.getLocale());
      } catch (IllegalArgumentException e) {
        // Invalid locale has somehow ended up in the database
        logger.log(Level.SEVERE, "Invalid locale found from User", e);
      }
    }
    
    if (locale == null) {
      locale = Locale.getDefault();;
    }
	}
	
	@PreDestroy
	public void preDestroy() {
	  if (isLoggedIn()) {
      logger.fine("User #" + getLoggedUserId() + " session ended");
	    logout(); 
	  } else {
      logger.fine("Anonymous user session ended");
	  }
	}

  public boolean isLoggedIn() {
    return loggedUserId != null;
  }
  
  public Role[] getLoggedUserRoles() {
    User loggedUser = getLoggedUser();
    if (loggedUser != null) {
    	return permissionController.listUserRoles(loggedUser).toArray(new Role[0]);
    } else {
      return new Role[] {
        Role.ANONYMOUS
      };
    }
  }
  
  public boolean hasLoggedUserRole(Role role) {
  	Role[] roles = getLoggedUserRoles();
  	for (Role loggedUserRole : roles) {
  		if (loggedUserRole == role) {
  			return true;
  		}
  	}
  	
  	return false;
  }

	public boolean hasLoggedUserPermission(Permission permission) {
		Role[] roles = getLoggedUserRoles();
		for (Role role : roles) {
			Permission[] rolePermissions = role.getPermissions();
			for (Permission rolePermission : rolePermissions) {
				if (rolePermission.equals(permission)) {
					return true;
				}
			}
		}
		
		return false;
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
    this.loggedUserTokenId = token.getId();
    login(token.getUserIdentifier().getUser());
  }
  
  public void login(User user) {
    this.loggedUserId = user.getId();
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
    return locale;
  }
  
  public void setLocale(Locale locale) {
    this.locale = locale;
  }


  public void setRedirectUrl(String redirectUrl) {
    this.redirectUrl = redirectUrl;
  }

  public String getRedirectUrl() {
    return redirectUrl;
  }
  
  public Token getLoginRequestToken() {
    return loginRequestToken;
  }
  
  public void setLoginRequestToken(Token loginRequestToken) {
    this.loginRequestToken = loginRequestToken;
  }
  
  public String[] getLoginScopes() {
    return loginScopes;
  }
  
  public void setLoginScopes(String[] loginScopes) {
    this.loginScopes = loginScopes;
  }
  
  public String getSessionId() {
    return session.getId();
  }
  
  private Long loggedUserId;
  private Locale locale;
  private Long loggedUserTokenId;
  private String redirectUrl;
  private Token loginRequestToken;
  private String[] loginScopes;
}

package fi.foyt.fni.session;

import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.scribe.model.Token;

import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.Role;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserToken;

@SessionScoped
@Stateful
public class SessionController implements Serializable {
  
	private static final long serialVersionUID = -441183766079031359L;
	
	@Inject
	private Logger logger;

	@Inject
	@Login
	private Event<UserSessionEvent> loginEvent;
	
	@Inject
	@Logout
	private Event<UserSessionEvent> logoutEvent;
	
	@PreDestroy
	public void preDestroy() {
	  if (isLoggedIn()) {
      logger.info("User #" + getLoggedUserId() + " session ended");
	    logout(); 
	  } else {
      logger.info("Anonymous user session ended");
	  }
	}

	@Inject
  private UserDAO userDAO;

  @Inject
  private UserTokenDAO userTokenDAO;

  public boolean isLoggedIn() {
    return loggedUserId != null;
  }
  
  public Role[] getLoggedUserRoles() {
    User loggedUser = getLoggedUser();
    if (loggedUser != null) {
    	return convertUserRole(loggedUser.getRole());
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
  
  private Role[] convertUserRole(UserRole role) {
  	switch (role) {
  		case ADMINISTRATOR:
  			return new Role[] {
  				Role.USER,
          Role.ILLUSION_USER,
          Role.MATERIAL_USER,
  				Role.FORUM_ADMIN,
  				Role.GAME_LIBRARY_MANAGER,
  				Role.SYSTEM_ADMINISTRATOR
  			};
  		case LIBRARIAN:
  		  return new Role[] {
          Role.USER,
          Role.ILLUSION_USER,
          Role.MATERIAL_USER,
          Role.FORUM_USER,
          Role.GAME_LIBRARY_MANAGER
        };
  		case USER:
  			return new Role[] {
          Role.USER,
          Role.ILLUSION_USER,
          Role.MATERIAL_USER,
          Role.FORUM_USER,
          Role.GAME_LIBRARY_USER
        };
  		case GUEST:
  			return new Role[] {
          Role.GUEST
        };
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
      
      if (user != null && StringUtils.isNotBlank(user.getLocale())) {
        try {
          return LocaleUtils.toLocale(user.getLocale());
        } catch (IllegalArgumentException e) {
          // Invalid locale has somehow ended up in the database
          logger.log(Level.SEVERE, "Invalid locale found from User", e);
        }
      }
      
      return Locale.getDefault();
    }
    
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
  
  private Long loggedUserId = null;
  private Locale locale = null;
  private Long loggedUserTokenId;
  private String redirectUrl;
  private Token loginRequestToken;
  private String[] loginScopes;
}

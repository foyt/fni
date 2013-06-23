package fi.foyt.fni.utils.auth;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserToken;

public class AuthUtils {
  
  private static AuthUtils INSTANCE = new AuthUtils();
  
  public static AuthUtils getInstance() {
    return INSTANCE;
  }
	
	private static final String REDIRECT_URL_SESSION_PARAMETER = "_REDIRECT_URL_";
	
	public boolean isAllowed(User user, UserRole requiredRole) {
		if (user == null)
			return false;
		
		if (requiredRole == UserRole.ADMINISTRATOR) {
			return user.getRole() == UserRole.ADMINISTRATOR;
		}
		
	  if (requiredRole == UserRole.USER) {
	  	if ((user.getRole() == UserRole.ADMINISTRATOR) || (user.getRole() == UserRole.USER))
	  		return true;
	  }
			
	  return false;
	}
	
  public String[] getGrantedScopes(UserToken userToken) {
    String grantedScopes = userToken.getGrantedScopes();
    if (StringUtils.isBlank(grantedScopes))
      return new String[0];
    return grantedScopes.split(",");
  }
  
  public boolean isGrantedScope(UserToken userToken, String scope) {
    String[] grantedScopes = getGrantedScopes(userToken);
    for (String grantedScope : grantedScopes) {
      if (grantedScope.equals(scope))
        return true;
    }
    
    return false;
  }

  public boolean isExpired(UserToken userToken) {
    if (userToken.getExpires() != null) {
      return System.currentTimeMillis() > userToken.getExpires().getTime();
    }
    
    return true;
  }

  public void storeRedirectUrl(HttpSession session, String redirectUrl) {
    session.setAttribute(REDIRECT_URL_SESSION_PARAMETER, redirectUrl);
  }

  public String retrieveRedirectUrl(HttpSession session) {
    String redirectUrl = (String) session.getAttribute(REDIRECT_URL_SESSION_PARAMETER);
    session.removeAttribute(REDIRECT_URL_SESSION_PARAMETER);
    return redirectUrl;
  }
}

package fi.foyt.fni.utils.auth;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.users.UserToken;

public class AuthUtils {
  
  public static String[] getGrantedScopes(UserToken userToken) {
    String grantedScopes = userToken.getGrantedScopes();
    if (StringUtils.isBlank(grantedScopes))
      return new String[0];
    return grantedScopes.split(",");
  }
  
  public static boolean isGrantedScope(UserToken userToken, String scope) {
    String[] grantedScopes = getGrantedScopes(userToken);
    for (String grantedScope : grantedScopes) {
      if (grantedScope.equals(scope))
        return true;
    }
    
    return false;
  }

  public static boolean isExpired(UserToken userToken) {
    if (userToken.getExpires() != null) {
      return System.currentTimeMillis() > userToken.getExpires().getTime();
    }
    
    return true;
  }
}

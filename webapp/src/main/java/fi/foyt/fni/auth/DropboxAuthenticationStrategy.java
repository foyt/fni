package fi.foyt.fni.auth;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DropBoxApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@RequestScoped
public class DropboxAuthenticationStrategy extends OAuthAuthenticationStrategy {
  
	@Inject
	private SystemSettingsController systemSettingsController;

	@Override
	protected String getApiKey() {
		return systemSettingsController.getSetting("auth.dropbox.apiKey");
	}

	@Override
	protected String getApiSecret() {
		return systemSettingsController.getSetting("auth.dropbox.apiSecret");
	}

	@Override
	protected String getCallbackUrl() {
		return systemSettingsController.getSetting("auth.dropbox.callbackUrl");
	}

	@Override
	protected String[] getRequiredScopes() {
		return null;
	}
  
  @Override
  public boolean getSupportLogin() {
    return false;
  }

  protected java.lang.Class<? extends org.scribe.builder.api.Api> getApiClass() {
    return DropBoxApi.class;
  };
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "oauth_token");
  }

  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      JSONObject userInfoObject = new JSONObject(OAuthUtils.doGetRequest(service, accessToken, "https://api.dropbox.com/1/account/info").getBody());
      String uid = userInfoObject.getString("uid");
      String displayName = userInfoObject.getString("display_name");
      String[] names = splitNames(displayName);
      String country = userInfoObject.getString("country");
      Locale userLocale = locale;
      String sourceId = displayName;
      
      if (StringUtils.isNotBlank(country)) {
        country = locale.getLanguage();
        userLocale = new Locale(country);
      }

      return loginUser(AuthSource.DROPBOX, sourceId, accessToken.getToken(), accessToken.getSecret(), null, uid, null, names[0], names[1], null, userLocale, null);
    } catch (JSONException e) {
    	throw new ExternalLoginFailedException();
    }
  }
  
  private static String[] splitNames(String displayName) {
    int lastNameIndex = displayName.lastIndexOf(' ');
    String firstName = "";
    String lastName = "";
    
    if (lastNameIndex > -1) {
      firstName = displayName.substring(0, lastNameIndex);
      lastName = displayName.substring(lastNameIndex + 1);
    } else {
      firstName = displayName;
    }
    
    return new String[] {
      firstName,
      lastName
    };
  }
}
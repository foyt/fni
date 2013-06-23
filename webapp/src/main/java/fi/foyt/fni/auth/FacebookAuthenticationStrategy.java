package fi.foyt.fni.auth;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@RequestScoped
public class FacebookAuthenticationStrategy extends OAuthAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;

	@Override
	protected String getApiKey() {
		return systemSettingsController.getSetting("auth.facebook.apiKey");
	}

	@Override
	protected String getApiSecret() {
		return systemSettingsController.getSetting("auth.facebook.apiSecret");
	}

	@Override
	protected String getCallbackUrl() {
		return systemSettingsController.getSetting("auth.facebook.callbackUrl");
	}

	@Override
	protected String[] getRequiredScopes() {
		return new String[] {
		  "email"
		};
	}
  
  @Override
  public boolean getSupportLogin() {
    return true;
  }

  protected java.lang.Class<? extends org.scribe.builder.api.Api> getApiClass() {
    return FacebookApi.class;
  };
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "code");
  }

  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      JSONObject meObject = new JSONObject(OAuthUtils.doGetRequest(service, accessToken, "https://graph.facebook.com/me").getBody());
      Integer expiresIn = extractExpires(accessToken);
      Date expires = null;
      if (expiresIn != null) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, expiresIn);
        expires = calendar.getTime(); 
      }
      
      String localeParam = meObject.getString("locale");
      String id = meObject.getString("id");
      String firstName = meObject.getString("first_name");
      String lastName = meObject.getString("last_name");
      String email = meObject.getString("email");
      String username = meObject.getString("username");
      Locale userLocale = null;
      
      if (StringUtils.isNotBlank(localeParam))
        userLocale = new Locale(localeParam);
      
      return loginUser(AuthSource.FACEBOOK, username, accessToken.getToken(), accessToken.getSecret(), expires, id, Arrays.asList(email), firstName, lastName, null, userLocale, grantedScopes);
    } catch (JSONException e) {
    	throw new ExternalLoginFailedException();
    }
  };
  
  private Integer extractExpires(Token accessToken) {
    try {
      Pattern pattern = Pattern.compile("expires=[0-9]*");
      Matcher matcher = pattern.matcher(accessToken.getRawResponse());
      if (matcher.find()) {
        String[] split = matcher.group().split("=");
        if (split.length == 2) 
          return NumberUtils.createInteger(split[1]);
      }
      
      return null;
    } catch (Exception e) {
      return null;
    }
  }

}
package fi.foyt.fni.auth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@RequestScoped
public class YahooAuthenticationStrategy extends OAuthAuthenticationStrategy {
	
	@Inject
	private SystemSettingsController systemSettingsController;
	
	@Override
	protected String getApiKey() {
		return systemSettingsController.getSetting("auth.yahoo.apiKey");
	}

	@Override
	protected String getApiSecret() {
		return systemSettingsController.getSetting("auth.yahoo.apiSecret");
	}

	@Override
	protected String getCallbackUrl() {
		return systemSettingsController.getSetting("auth.yahoo.callbackUrl");
	}

	@Override
	protected String[] getRequiredScopes() {
		return null;
	}

  
  @Override
  public boolean getSupportLogin() {
    return true;
  }

  protected java.lang.Class<? extends org.scribe.builder.api.Api> getApiClass() {
    return YahooApi.class;
  };
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "oauth_verifier");
  }
  
  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException,
  		EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      Date expires = null;
      Integer expiresIn = extractExpires(accessToken);
      if (expiresIn != null) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, expiresIn);
        expires = calendar.getTime(); 
      }
      
      JSONObject guidObject = new JSONObject(OAuthUtils.doGetRequest(service, accessToken, "http://social.yahooapis.com/v1/me/guid?format=json").getBody());
      String guid = guidObject.getJSONObject("guid").getString("value");
      
      JSONObject profileObject = new JSONObject(OAuthUtils.doGetRequest(service, accessToken, "http://social.yahooapis.com/v1/user/" + guid + "/profile?format=json").getBody()).getJSONObject("profile");
      
      List<String> emails = new ArrayList<String>();
      String nickname = profileObject.optString("nickname");
      String familyName = profileObject.optString("familyName");
      String givenName = profileObject.optString("givenName");
      
      if (StringUtils.isBlank(givenName) && StringUtils.isNotBlank(nickname))
        givenName = nickname;
      
      JSONArray emailsJson = profileObject.getJSONArray("emails");
      for (int i = 0, l = emailsJson.length(); i < l; i++) {
        JSONObject emailObject = emailsJson.getJSONObject(i);
        emails.add(emailObject.getString("handle"));
      }
      
      String sourceId = emails.size() > 0 ? emails.get(0) : givenName + (StringUtils.isNotBlank(familyName) ? ' ' + familyName : "");
      
      return loginUser(AuthSource.YAHOO, sourceId, accessToken.getToken(), accessToken.getSecret(), expires, guid, emails, familyName, givenName, nickname, null, grantedScopes);
    } catch (JSONException e) {
    	throw new ExternalLoginFailedException();
    }
  };
  
  private Integer extractExpires(Token accessToken) {
    try {
      Pattern pattern = Pattern.compile("oauth_authorization_expires_in=[0-9]*");
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
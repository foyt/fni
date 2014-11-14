package fi.foyt.fni.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.scribe.builder.api.YahooApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@Dependent
public class YahooAuthenticationStrategy extends OAuthAuthenticationStrategy {
	
	@Inject
	private SystemSettingsController systemSettingsController;

  @Override
  public AuthSource getAuthSource() {
    return AuthSource.YAHOO;
  }

	@Override
	protected String getApiKey() {
		return systemSettingsController.getSetting(SystemSettingKey.YAHOO_APIKEY);
	}

	@Override
	protected String getApiSecret() {
		return systemSettingsController.getSetting(SystemSettingKey.YAHOO_APISECRET);
	}

	@Override
	protected String getCallbackUrl() {
		return systemSettingsController.getSetting(SystemSettingKey.YAHOO_CALLBACKURL);
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
      
      ObjectMapper objectMapper = new ObjectMapper();
      
      YahooGuid guid = objectMapper.readValue(OAuthUtils.doGetRequest(service, accessToken, "http://social.yahooapis.com/v1/me/guid?format=json").getBody(), YahooGuid.class);
      Map<String, Object> response = objectMapper.readValue(OAuthUtils.doGetRequest(service, accessToken, "http://social.yahooapis.com/v1/user/ " + guid.getValue() + "/profile?format=json").getBody(), new TypeReference<Map<String, Object>>() { });
      @SuppressWarnings("unchecked")
      Map<String, Object> profileObject = (Map<String, Object>) response.get("profile");
      
      List<String> emails = new ArrayList<String>();
      String nickname = (String) profileObject.get("nickname");
      String familyName = (String) profileObject.get("familyName");
      String givenName = (String) profileObject.get("givenName");
      
      if (StringUtils.isBlank(givenName) && StringUtils.isNotBlank(nickname))
        givenName = nickname;
      
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> emailsJson = (List<Map<String, Object>>) profileObject.get("emails");
      for (Map<String, Object> emailObject : emailsJson) {
        emails.add((String) emailObject.get("handle"));
      }
      
      String sourceId = emails.size() > 0 ? emails.get(0) : givenName + (StringUtils.isNotBlank(familyName) ? ' ' + familyName : "");
      
      return loginUser(AuthSource.YAHOO, sourceId, accessToken.getToken(), accessToken.getSecret(), expires, guid.getValue(), emails, familyName, givenName, nickname, null, grantedScopes);
    } catch (IOException e) {
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

  @SuppressWarnings ("unused")
  private static class YahooGuid {
    
    public String getUri() {
      return uri;
    }
    
    public void setUri(String uri) {
      this.uri = uri;
    }
    
    public String getValue() {
      return value;
    }
    
    
    public void setValue(String value) {
      this.value = value;
    }
    
    private String uri;
    private String value;
  }
}
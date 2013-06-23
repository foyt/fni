package fi.foyt.fni.auth;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.api.DefaultApi20;
import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuth20ServiceImpl;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@RequestScoped
public class GoogleAuthenticationStrategy extends OAuthAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;
	
  @Override
  protected String getApiKey() {
  	return systemSettingsController.getSetting("auth.google.apiKey");
  }
  
  @Override
  protected String getApiSecret() {
  	return systemSettingsController.getSetting("auth.google.apiSecret");
  }
  
  @Override
  protected String getCallbackUrl() {
  	return systemSettingsController.getSetting("auth.google.callbackUrl");
  }
  
  @Override
  protected String[] getRequiredScopes() {
  	return new String[] {
  		"https://www.googleapis.com/auth/userinfo.email", "https://www.googleapis.com/auth/userinfo.profile"
  	};
  }
  
  @Override
  public boolean getSupportLogin() {
    return true;
  }

  protected java.lang.Class<? extends org.scribe.builder.api.Api> getApiClass() {
    return GoogleApi20.class;
  };
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "code");
  }
  
  public static class GoogleApi20 extends DefaultApi20 {
    private static final String AUTHORIZATION_URL = "https://accounts.google.com/o/oauth2/auth?client_id=%s&response_type=code&redirect_uri=%s&scope=%s";

    @Override
    public String getAccessTokenEndpoint() {
      return "https://accounts.google.com/o/oauth2/token";
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
      try {
        String callback = new String(URLCodec.encodeUrl(null, config.getCallback().getBytes("UTF-8")), "UTF-8");
        String scope = new String(URLCodec.encodeUrl(null, config.getScope().getBytes("UTF-8")), "UTF-8");
        return String.format(AUTHORIZATION_URL, config.getApiKey(), callback, scope);
      } catch (UnsupportedEncodingException e) {
        return null;
      }
    }

    @Override
    public AccessTokenExtractor getAccessTokenExtractor() {
      return new JsonTokenExtractor();
    }

    @Override
    public Verb getAccessTokenVerb() {
      return Verb.POST;
    }

    @Override
    public OAuthService createService(OAuthConfig config) {
      return new GoogleService(this, config);
    }
  }

  public static class GoogleService extends OAuth20ServiceImpl {
    
    public GoogleService(DefaultApi20 api, OAuthConfig config) {
      super(api, config);
      
      this.api = api;
      this.config = config;
    }

    @Override
    public Token getAccessToken(Token requestToken, Verifier verifier) {
      OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
      request.addBodyParameter(OAuthConstants.CLIENT_ID, config.getApiKey());
      request.addBodyParameter(OAuthConstants.CLIENT_SECRET, config.getApiSecret());
      request.addBodyParameter(OAuthConstants.CODE, verifier.getValue());
      request.addBodyParameter(OAuthConstants.REDIRECT_URI, config.getCallback());
      request.addBodyParameter("grant_type", "authorization_code");
      
      if (config.hasScope())
        request.addBodyParameter(OAuthConstants.SCOPE, config.getScope());
      
      Response response = request.send();

      try {
        JSONObject jsonObject = new JSONObject(response.getBody());
        return api.getAccessTokenExtractor().extract(jsonObject.toString(0));
      } catch (JSONException e) {
        return null;
      }
    }
    
    private OAuthConfig config;
    private DefaultApi20 api;
  }
  
  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException,
  		EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      JSONObject rawJson = new JSONObject(accessToken.getRawResponse());
      int expiresIn = rawJson.getInt("expires_in");
      
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(new Date());
      calendar.add(Calendar.SECOND, expiresIn);
      Date expires = calendar.getTime();

      // Requires https://www.googleapis.com/auth/userinfo.email and https://www.googleapis.com/auth/userinfo.profile scopes
      
      JSONObject userInfoObject = new JSONObject(OAuthUtils.doGetRequest(service, accessToken, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json").getBody());
      String identifier = userInfoObject.getString("id");
      String email = userInfoObject.getString("email");
      String firstName = userInfoObject.getString("given_name");
      String lastName = userInfoObject.getString("family_name");
      String localeString = userInfoObject.optString("locale");
      Locale userLocale = null;
      
      if (StringUtils.isNotBlank(localeString)) {
        userLocale = new Locale(localeString);
      }

      return loginUser(AuthSource.GOOGLE, email, accessToken.getToken(), accessToken.getSecret(), expires, identifier, Arrays.asList(email), firstName, lastName, null, userLocale, grantedScopes);
    } catch (JSONException e) {
    	throw new ExternalLoginFailedException();
    }
  };

}

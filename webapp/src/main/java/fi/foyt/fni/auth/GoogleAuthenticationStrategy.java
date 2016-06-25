package fi.foyt.fni.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.codec.net.URLCodec;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.api.Api;
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
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

public class GoogleAuthenticationStrategy extends OAuthAuthenticationStrategy {
  
	@Inject
	private SystemSettingsController systemSettingsController;

  @Override
  public AuthSource getAuthSource() {
    return AuthSource.GOOGLE;
  }

  @Override
  protected String getApiKey() {
  	return systemSettingsController.getSetting(SystemSettingKey.GOOGLE_APIKEY);
  }
  
  @Override
  protected String getApiSecret() {
  	return systemSettingsController.getSetting(SystemSettingKey.GOOGLE_APISECRET);
  }
  
  @Override
  protected String getCallbackUrl() {
  	return systemSettingsController.getSetting(SystemSettingKey.GOOGLE_CALLBACKURL);
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
  
  @Override
  protected Api getApi() {
    return new GoogleApi20();
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
    private static final Logger logger = Logger.getLogger(GoogleApi20.class.getName());

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
        logger.log(Level.SEVERE, "Unsupported encoding", e);
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

    private static final Logger logger = Logger.getLogger(GoogleService.class.getName());
    
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

      ObjectMapper objectMapper = new ObjectMapper();
      try {
        String tokenJson = objectMapper.writeValueAsString(objectMapper.readTree(response.getBody()));
        return api.getAccessTokenExtractor().extract(tokenJson);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Unsupported encoding", e);
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
      ObjectMapper objectMapper = new ObjectMapper();
      
      GoogleAccessToken rawAccessToken = objectMapper.readValue(accessToken.getRawResponse(), GoogleAccessToken.class);
      
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(new Date());
      calendar.add(Calendar.SECOND, rawAccessToken.getExpiresIn());
      Date expires = calendar.getTime();

      // Requires https://www.googleapis.com/auth/userinfo.email and https://www.googleapis.com/auth/userinfo.profile scopes
      
      String response = OAuthUtils.doGetRequest(service, accessToken, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json").getBody();
      GoogleUserInfo userInfoObject = objectMapper.readValue(response, GoogleUserInfo.class);
      String identifier = userInfoObject.getId();
      String email = userInfoObject.getEmail();
      String firstName = userInfoObject.getGivenName();
      String lastName = userInfoObject.getFamilyName();
      Locale userLocale = userInfoObject.getLocale();

      return loginUser(AuthSource.GOOGLE, email, accessToken.getToken(), accessToken.getSecret(), expires, identifier, Arrays.asList(email), firstName, lastName, null, userLocale, grantedScopes);
    } catch (IOException e) {
    	throw new ExternalLoginFailedException(e);
    }
  };

  @SuppressWarnings ("unused")
  @JsonIgnoreProperties (ignoreUnknown = true)
  private static class GoogleAccessToken {
    
    public Integer getExpiresIn() {
      return expiresIn;
    }
    
    public void setExpiresIn(Integer expiresIn) {
      this.expiresIn = expiresIn;
    }
    
    @JsonProperty ("expires_in")
    private Integer expiresIn;
  }
  
  @SuppressWarnings ("unused")
  @JsonIgnoreProperties (ignoreUnknown = true)
  private static class GoogleUserInfo {
    
    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getGivenName() {
      return givenName;
    }

    public void setGivenName(String givenName) {
      this.givenName = givenName;
    }

    public String getFamilyName() {
      return familyName;
    }

    public void setFamilyName(String familyName) {
      this.familyName = familyName;
    }

    public Locale getLocale() {
      return locale;
    }

    public void setLocale(Locale locale) {
      this.locale = locale;
    }

    private String id;
    
    private String email;
    
    @JsonProperty ("given_name")
    private String givenName;
    
    @JsonProperty ("family_name")
    private String familyName;
    
    private Locale locale;
  }
}

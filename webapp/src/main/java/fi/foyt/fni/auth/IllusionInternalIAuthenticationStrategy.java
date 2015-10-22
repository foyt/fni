package fi.foyt.fni.auth;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.api.Api;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.rest.users.model.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

public class IllusionInternalIAuthenticationStrategy extends OAuthAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;

  @Inject
	private HttpServletRequest request;

  @Inject
	private IllusionEventController illusionEventController;

  @Override
  public AuthSource getAuthSource() {
    return AuthSource.ILLUSION_INTERNAL;
  }

  @Override
  protected String getApiKey() {
  	return getClientId();
  }
  
  @Override
  protected String getApiSecret() {
  	return getClientSecret();
  }
  
  @Override
  protected String getCallbackUrl() {
    return getRedirectUrl();
  }
  
  @Override
  protected String[] getRequiredScopes() {
  	return new String[] {};
  }
  
  @Override
  public boolean getSupportLogin() {
    return true;
  }
  
  @Override
  protected Api getApi() {
    return new FnIApi20(systemSettingsController.getSiteUrl(true, true));
  }
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "code");
  }
  
  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException,
  		EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
      String siteUrl = systemSettingsController.getSiteUrl(true, true);
      
      ObjectMapper objectMapper = new ObjectMapper();
      
      String response = OAuthUtils.doGetRequest(service, accessToken, new StringBuilder(siteUrl).append("/rest/users/users/me").toString()).getBody();
      User userInfo = objectMapper.readValue(response, User.class);
      String sourceId = userInfo.getFirstName() + " " + userInfo.getLastName();
        
      AccessToken rawAccessToken = objectMapper.readValue(accessToken.getRawResponse(), AccessToken.class);
      
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(new Date());
      calendar.add(Calendar.SECOND, rawAccessToken.getExpiresIn());
      Date expires = calendar.getTime();

      return loginUser(AuthSource.ILLUSION_INTERNAL, sourceId, accessToken.getToken(), accessToken.getSecret(), expires, userInfo.getId().toString(), userInfo.getEmails(), userInfo.getFirstName(), userInfo.getLastName(), null, null, grantedScopes);
    } catch (IOException e) {
    	throw new ExternalLoginFailedException(e);
    }
  }
  
  private OAuthClient getOAuthClient() {
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventByDomain(request.getServerName());
    if ((illusionEvent != null) && (illusionEvent.getOAuthClient() != null)) {
      return illusionEvent.getOAuthClient();
    }
    
    return null;
  }
  
  private String getClientId() {
    OAuthClient client = getOAuthClient();
    if (client != null) {
      return client.getClientId();
    }
    
    return null;
  }
  
  private String getClientSecret() {
    OAuthClient client = getOAuthClient();
    if (client != null) {
      return client.getClientSecret();
    }
    
    return null;
  }
  
  private String getRedirectUrl() {
    OAuthClient client = getOAuthClient();
    if (client != null) {
      return client.getRedirectUrl();
    }
    
    return null;
  }
  
  @SuppressWarnings ("unused")
  @JsonIgnoreProperties (ignoreUnknown = true)
  private static class AccessToken {
    
    public Integer getExpiresIn() {
      return expiresIn;
    }
    
    public void setExpiresIn(Integer expiresIn) {
      this.expiresIn = expiresIn;
    }
    
    @JsonProperty ("expires_in")
    private Integer expiresIn;
  }
}

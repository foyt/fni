package fi.foyt.fni.auth;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
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
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.rest.users.model.User;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.utils.auth.OAuthUtils;

@RequestScoped
@Stateful
public class IllusionInternalIAuthenticationStrategy extends OAuthAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;

  @Inject
	private HttpServletRequest request;

  @Inject
	private IllusionEventController illusionEventController;
	
	@PostConstruct
	public void init() {
	  IllusionEvent illusionEvent = illusionEventController.findIllusionEventByDomain(request.getServerName());
	  if ((illusionEvent != null) && (illusionEvent.getOAuthClient() != null)) {
      clientId = illusionEvent.getOAuthClient().getClientId();
      clientSecret = illusionEvent.getOAuthClient().getClientSecret();
      redirectUrl = illusionEvent.getOAuthClient().getRedirectUrl();
	  }
	  
	  siteUrl = systemSettingsController.getSiteUrl(true, true);
	}

  @Override
  public AuthSource getAuthSource() {
    return AuthSource.ILLUSION_INTERNAL;
  }

  @Override
  protected String getApiKey() {
  	return clientId;
  }
  
  @Override
  protected String getApiSecret() {
  	return clientSecret;
  }
  
  @Override
  protected String getCallbackUrl() {
    return redirectUrl;
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
    return new FnIApi20(siteUrl);
  }
  
  @Override
  protected String getVerifier(Map<String, String[]> parameters) {
    return getParameter(parameters, "code");
  }
  
  @Override
  protected UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException,
  		EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    try {
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
    	throw new ExternalLoginFailedException();
    }
  };
  
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
  
  private String clientId;
  private String clientSecret;
  private String redirectUrl;
  private String siteUrl;
}

package fi.foyt.fni.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.Api;
import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import fi.foyt.fni.persistence.model.users.UserToken;

public abstract class OAuthAuthenticationStrategy extends AuthenticationStrategy {
  
  private static final String REQUEST_TOKEN_SESSION_PARAMETER = "_OAUTH_REQUEST_TOKEN_";
  private static final String SCOPES_SESSION_PARAMETER = "_OAUTH_REQUEST_SCOPES_";
  
  public String authorize(HttpSession session, String... extraScopes) throws ConfigurationErrorException {
    String[] scopes = null;
    String[] requiredScopes = getRequiredScopes();

    if (requiredScopes != null && requiredScopes.length > 0) {
      if (extraScopes != null && extraScopes.length > 0) {
        scopes = new String[requiredScopes.length + extraScopes.length];
        System.arraycopy(requiredScopes, 0, scopes, 0, requiredScopes.length);
        System.arraycopy(extraScopes, 0, scopes, requiredScopes.length, extraScopes.length);
      } else {
        scopes = requiredScopes;
      }
    } else {
      scopes = extraScopes;
    }
    
    OAuthService service = getOAuthService(scopes);
    
    Token requestToken = null;
    
    boolean isV1 = DefaultApi10a.class.isAssignableFrom(getApiClass());
    if (isV1) {
      requestToken = service.getRequestToken();
    }
    
    String authUrl = service.getAuthorizationUrl(requestToken);
    
    if (isV1) {
      if (StringUtils.isNotBlank(getCallbackUrl())) {
        try {
          authUrl += "&oauth_callback=" + URLEncoder.encode(getCallbackUrl(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        	throw new ConfigurationErrorException(e);
        }
      }
    }
    
    session.setAttribute(REQUEST_TOKEN_SESSION_PARAMETER, requestToken);
    session.setAttribute(SCOPES_SESSION_PARAMETER, scopes);
   
    return authUrl;
  }
  
	@Override
  public UserToken accessToken(HttpSession session, Locale locale, Map<String, String[]> parameters) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException {
    Token requestToken = (Token) session.getAttribute(REQUEST_TOKEN_SESSION_PARAMETER);
    String[] scopes = (String[]) session.getAttribute(SCOPES_SESSION_PARAMETER);
    session.removeAttribute(REQUEST_TOKEN_SESSION_PARAMETER);
    session.removeAttribute(SCOPES_SESSION_PARAMETER);
    
    OAuthService service = getOAuthService(scopes);
    Verifier verifier = new Verifier(getVerifier(parameters));
    Token accessToken = service.getAccessToken(requestToken, verifier);
    
    return handleLogin(locale, service, accessToken, scopes);
  }
  
  protected abstract String getVerifier(Map<String, String[]> parameters);
  
  protected abstract UserToken handleLogin(Locale locale, OAuthService service, Token accessToken, String[] grantedScopes) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException;
  
  protected abstract Class<? extends Api> getApiClass();
  
  protected abstract String getApiKey();
  
  protected abstract String getApiSecret();

  protected abstract String getCallbackUrl();
  
  protected abstract String[] getRequiredScopes();
  
  public OAuthService getOAuthService(String... scopes) {
    ServiceBuilder serviceBuilder = new ServiceBuilder().provider(getApiClass())
      .apiKey(getApiKey())
      .apiSecret(getApiSecret())
      .callback(getCallbackUrl());
     
    if ((scopes != null) && (scopes.length > 0)) {
      StringBuilder scopeBuilder = new StringBuilder();
      for (int i = 0, l = scopes.length; i < l;i++) {
        scopeBuilder.append(scopes[i]);
        if (i < (l - 1))
          scopeBuilder.append(' ');
      }
      serviceBuilder = serviceBuilder.scope(scopeBuilder.toString());
    }
    
    return serviceBuilder.build();
  }

}

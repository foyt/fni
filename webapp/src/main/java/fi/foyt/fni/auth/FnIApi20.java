package fi.foyt.fni.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.net.URLCodec;
import org.codehaus.jackson.map.ObjectMapper;
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

public class FnIApi20 extends DefaultApi20 {
  
  public FnIApi20(String siteUrl) {
    this.siteUrl = siteUrl;
  }
  
  @Override
  public String getAccessTokenEndpoint() {
    return new StringBuilder(siteUrl).append("/oauth2/token").toString();
  }

  @Override
  public String getAuthorizationUrl(OAuthConfig config) {
    try {
      String callback = new String(URLCodec.encodeUrl(null, config.getCallback().getBytes("UTF-8")), "UTF-8");
      
      String authorizationUrl = new StringBuilder(siteUrl)
        .append("/oauth2/authorize?response_type=code&client_id=").append(config.getApiKey()).append("&redirect_uri=").append(callback)
        .toString();
      
      return authorizationUrl;
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
    return new FnIService(this, config);
  }
  
  private String siteUrl;
  
  private class FnIService extends OAuth20ServiceImpl {
    
    public FnIService(DefaultApi20 api, OAuthConfig config) {
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
        return null;
      }
    }
    
    private OAuthConfig config;
    private DefaultApi20 api;
  }
}
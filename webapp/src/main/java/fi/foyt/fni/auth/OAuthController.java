package fi.foyt.fni.auth;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.oauth.OAuthAccessTokenDAO;
import fi.foyt.fni.persistence.dao.oauth.OAuthAuthorizationCodeDAO;
import fi.foyt.fni.persistence.dao.oauth.OAuthClientDAO;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.oauth.OAuthAuthorizationCode;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.oauth.OAuthClientType;
import fi.foyt.fni.persistence.model.users.User;

public class OAuthController {

  @Inject
  private OAuthAccessTokenDAO oAuthAccessTokenDAO;

  @Inject
  private OAuthAuthorizationCodeDAO oAuthAuthorizationCodeDAO;

  @Inject
  private OAuthClientDAO oAuthClientDAO;

  /* OAuthClient */
  
  public OAuthClient createClient(String name, OAuthClientType type, String clientId, String clientSecret, String redirectUrl) {
    return oAuthClientDAO.create(name, type, clientId, clientSecret, redirectUrl);
  }

  public OAuthClient findClientByClientId(String clientId) {
    return oAuthClientDAO.findByClientId(clientId);
  }

  public OAuthClient findClientByClientIdAndClientSecret(String clientId, String clientSecret) {
    return oAuthClientDAO.findByClientIdAndClientSecret(clientId, clientSecret);
  }
  
  /* OAuthAuthorizationCode */
  
  public OAuthAuthorizationCode createAuthorizationCode(OAuthClient client, User user, String code) {
    return oAuthAuthorizationCodeDAO.create(client, user, code);
  }

  public OAuthAuthorizationCode findAuthorizationCodeByClientAndCode(OAuthClient client, String code) {
    return oAuthAuthorizationCodeDAO.findByClientAndCode(client, code);
  }
  
  
  /* OAuthAccessToken */
  
  public OAuthAccessToken createAccessToken(OAuthClient client, OAuthAuthorizationCode authorizationCode, String accessToken, Long expires) {
    return oAuthAccessTokenDAO.create(client, authorizationCode, accessToken, expires); 
  }

  public OAuthAccessToken findAccessTokenByAccessToken(String accessToken) {
    return oAuthAccessTokenDAO.findByAccessToken(accessToken);
  }
}

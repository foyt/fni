package fi.foyt.fni.view.users;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.io.IOUtils;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import fi.foyt.fni.auth.OAuthController;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.oauth.OAuthAuthorizationCode;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.oauth.OAuthClientType;

@WebServlet(urlPatterns = "/oauth2/token", name = "oauth2-token")
@Transactional
public class OAuth2TokenServlet extends HttpServlet {
  
  private static final long serialVersionUID = 299062857600491172L;
  private static final long TOKEN_EXPIRES = 3600;
  
  @Inject
  private Logger logger;
  
  @Inject
  private OAuthController oAuthController;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      OAuthTokenRequest oAuthRequest = new OAuthTokenRequest(request);
      OAuthClient client = oAuthController.findClientByClientIdAndClientSecret(oAuthRequest.getClientId(), oAuthRequest.getClientSecret());
      if (client == null) {
        logger.warning("Invalid clientId or clientSecret");
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid clientId or clientSecret");
        return;
      }

      OAuthResponse authResponse = null;
      Long expires = (System.currentTimeMillis() / 1000l) + TOKEN_EXPIRES;
      OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
      String accessToken = oauthIssuerImpl.accessToken();
      String grantType = oAuthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).toUpperCase();
      
      switch (GrantType.valueOf(grantType)) {
        case AUTHORIZATION_CODE:
          String code = oAuthRequest.getParam(OAuth.OAUTH_CODE);
          OAuthAuthorizationCode authorizationCode = oAuthController.findAuthorizationCodeByClientAndCode(client, code);
          if (authorizationCode == null) {
            logger.warning("Invalid authorization code");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid authorization code");
            return;
          }
          
          OAuthAccessToken token = oAuthController.createAccessToken(authorizationCode, accessToken, expires);

          authResponse = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
            .setAccessToken(token.getAccessToken())
            .setExpiresIn(String.valueOf(TOKEN_EXPIRES))
            .buildJSONMessage();
          
        break;
        case CLIENT_CREDENTIALS:
          if (client.getType() != OAuthClientType.SERVICE) {
            logger.warning("Invalid client for grant client credentials grant type");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid client for grant client credentials grant type");
            return;
          }
          
          OAuthAccessToken serviceToken = oAuthController.createAccessToken(null, accessToken, expires);

          authResponse = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
            .setAccessToken(serviceToken.getAccessToken())
            .setExpiresIn(String.valueOf(TOKEN_EXPIRES))
            .buildJSONMessage();
        break;
        default:
          logger.log(Level.WARNING, "Received request for unimplemented grant type " + grantType);
          response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
          return;
      }
      
      response.setStatus(authResponse.getResponseStatus());
      IOUtils.write(authResponse.getBody(), response.getOutputStream());
    } catch (OAuthSystemException | OAuthProblemException e) {
      logger.log(Level.SEVERE, "Could not process oauth token request", e);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


}

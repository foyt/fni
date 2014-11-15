package fi.foyt.fni.view.users;

import java.io.IOException;

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

@WebServlet(urlPatterns = "/oauth2/token", name = "oauth2-token")
@Transactional
public class OAuth2TokenServlet extends HttpServlet {
  
  private static final long serialVersionUID = 299062857600491172L;
  private static final long TOKEN_EXPIRES = 3600;
  
  @Inject
  private OAuthController oAuthController;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try {
      OAuthTokenRequest oAuthRequest = new OAuthTokenRequest(request);
      OAuthClient client = oAuthController.findClientByClientIdAndClientSecret(oAuthRequest.getClientId(), oAuthRequest.getClientSecret());
      if (client == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid clientId or clientSecret");
        return;
      }

      OAuthAuthorizationCode authorizationCode = null;
      
      switch (GrantType.valueOf(oAuthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).toUpperCase())) {
        case AUTHORIZATION_CODE:
          String code = oAuthRequest.getParam(OAuth.OAUTH_CODE);
          authorizationCode = oAuthController.findAuthorizationCodeByClientAndCode(client, code);
          if (authorizationCode == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid authorization code");
            return;
          }
        break;
        case REFRESH_TOKEN:
          // TODO: Implement refresh token
          response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
          return;
        default:
          response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
          return;
      }
      
      Long expires = (System.currentTimeMillis() / 1000l) + TOKEN_EXPIRES;
      OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
      String accessToken = oauthIssuerImpl.accessToken();
      OAuthAccessToken token = oAuthController.createAccessToken(authorizationCode, accessToken, expires);

      OAuthResponse authResponse = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
        .setAccessToken(token.getAccessToken())
        .setExpiresIn(String.valueOf(TOKEN_EXPIRES))
        .buildJSONMessage();
      
      response.setStatus(authResponse.getResponseStatus());
      IOUtils.write(authResponse.getBody(), response.getOutputStream());
    } catch (OAuthSystemException | OAuthProblemException e) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }


}

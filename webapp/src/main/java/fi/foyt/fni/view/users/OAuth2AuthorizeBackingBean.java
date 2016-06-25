package fi.foyt.fni.view.users;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.auth.OAuthController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.oauth.OAuthAuthorizationCode;
import fi.foyt.fni.persistence.model.oauth.OAuthClient;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;

@Join(path = "/oauth2/authorize", to = "/users/oauth2-authorize.jsf")
@RequestScoped
@Named ("oAuth2AuthorizeBackingBean")
@Stateful
@LoggedIn
public class OAuth2AuthorizeBackingBean {
  
  @Inject
  private Logger logger;

  @Inject
  private HttpServletRequest request;

  @Inject
  private OAuthController oAuthController;

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private NavigationController navigationController;
  
  @RequestAction
  public String init() {
    try {
      OAuthAuthzRequest oAuthRequest = new OAuthAuthzRequest(request);
      OAuthClient oAuthClient = oAuthController.findClientByClientId(oAuthRequest.getClientId());
      if (oAuthClient == null) {
        return navigationController.accessDenied();
      }
      
      setClientId(oAuthClient.getClientId());
      setClientName(oAuthClient.getName());
      setRequestType(oAuthRequest.getResponseType());

      if (illusionEventController.findIllusionEventByOAuthClient(oAuthClient) != null) {
        return "/users/oauth2-auto-authorize.jsf";
      }
    } catch (OAuthSystemException | OAuthProblemException e) {
      logger.log(Level.SEVERE, "OAuth error occurred", e);
      return navigationController.internalError();
    }

    return null;
  }

  public String authorize() {
    User loggedUser = sessionController.getLoggedUser();
    if (loggedUser == null) {
      return navigationController.accessDenied();
    }
    
    OAuthClient client = oAuthController.findClientByClientId(getClientId());
    if (client == null) {
      return navigationController.accessDenied();
    }
    
    OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
    OAuthAuthorizationCode authorizationCode;
    try {
      authorizationCode = oAuthController.createAuthorizationCode(client, loggedUser, oauthIssuerImpl.authorizationCode());
      OAuthASResponse.OAuthAuthorizationResponseBuilder responseBuilder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND)
          .location(client.getRedirectUrl())
          .setCode(authorizationCode.getCode());
      FacesContext.getCurrentInstance().getExternalContext().redirect(responseBuilder.buildQueryMessage().getLocationUri());
    } catch (IOException | OAuthSystemException e) {
      logger.log(Level.WARNING, "Failed to embed image as base64", e);
      return navigationController.internalError();
    }

    return null;
  }

  public String deny() {
    OAuthClient client = oAuthController.findClientByClientId(getClientId());
    if (client == null) {
      return navigationController.accessDenied();
    }
    
    try {
      OAuthASResponse.OAuthAuthorizationResponseBuilder responseBuilder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FORBIDDEN)
          .setParam("error", OAuthError.CodeResponse.ACCESS_DENIED)
          .location(client.getRedirectUrl());
      FacesContext.getCurrentInstance().getExternalContext().redirect(responseBuilder.buildQueryMessage().getLocationUri());
    } catch (IOException | OAuthSystemException e) {
      logger.log(Level.SEVERE, "OAuth error occurred", e);
      return navigationController.internalError();
    }

    return null;
  }

  public String getClientId() {
    return clientId;
  }
  
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }
  
  public String getClientName() {
    return clientName;
  }
  
  public void setClientName(String clientName) {
    this.clientName = clientName;
  }
  
  public String getRequestType() {
    return requestType;
  }
  
  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }
  
  private String clientId;
  private String clientName;
  private String requestType;
}

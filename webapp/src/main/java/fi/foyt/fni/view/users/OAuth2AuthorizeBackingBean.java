package fi.foyt.fni.view.users;

import java.io.IOException;

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
  private HttpServletRequest request;

  @Inject
  private OAuthController oAuthController;

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @RequestAction
  public String init() {
    try {
      OAuthAuthzRequest oAuthRequest = new OAuthAuthzRequest(request);
      OAuthClient oAuthClient = oAuthController.findClientByClientId(oAuthRequest.getClientId());
      if (oAuthClient == null) {
        return "/error/access-denied.jsf";
      }
      
      setClientId(oAuthClient.getClientId());
      setClientName(oAuthClient.getName());
      setRequestType(oAuthRequest.getResponseType());

      if (illusionEventController.findIllusionEventByOAuthClient(oAuthClient) != null) {
        return "/users/oauth2-auto-authorize.jsf";
      }
    } catch (OAuthSystemException | OAuthProblemException e) {
      return "/error/internal-error.jsf";
    }

    return null;
  }

  public String authorize() {
    User loggedUser = sessionController.getLoggedUser();
    if (loggedUser == null) {
      return "/error/access-denied.jsf";
    }
    
    OAuthClient client = oAuthController.findClientByClientId(getClientId());
    if (client == null) {
      return "/error/access-denied.jsf";
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
      return "/error/internal-error.jsf";
    }

    return null;
  }

  public String deny() {
    OAuthClient client = oAuthController.findClientByClientId(getClientId());
    if (client == null) {
      return "/error/access-denied.jsf";
    }
    
    try {
      OAuthASResponse.OAuthAuthorizationResponseBuilder responseBuilder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FORBIDDEN)
          .setParam("error", OAuthError.CodeResponse.ACCESS_DENIED)
          .location(client.getRedirectUrl());
      FacesContext.getCurrentInstance().getExternalContext().redirect(responseBuilder.buildQueryMessage().getLocationUri());
    } catch (IOException | OAuthSystemException e) {
      return "/error/internal-error.jsf";
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

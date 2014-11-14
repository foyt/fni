package fi.foyt.fni.rest;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.jboss.resteasy.core.ResourceMethodInvoker;

import fi.foyt.fni.auth.OAuthController;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.session.SessionController;

@Provider
public class SecurityFilter implements ContainerRequestFilter {

  @Inject
  private SessionController sessionController;
  
  @Context
  private HttpServletRequest request;

  @Inject
  private OAuthController oAuthController;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    ResourceMethodInvoker methodInvoker = (ResourceMethodInvoker) requestContext.getProperty("org.jboss.resteasy.core.ResourceMethodInvoker");
    Method method = methodInvoker.getMethod();
    if (method == null) {
      requestContext.abortWith(Response.status(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR).build());
    }
    
    if (!method.isAnnotationPresent(Unsecure.class)) {
      if (!sessionController.isLoggedIn()) {
        try {
          OAuthAccessResourceRequest oAuthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY, ParameterStyle.HEADER);
          if (oAuthRequest != null) {
            OAuthAccessToken accessToken = oAuthController.findAccessTokenByAccessToken(oAuthRequest.getAccessToken());
            if (accessToken == null) {
              requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
            }
  
            sessionController.login(accessToken.getAuthorizationCode().getUser());
          }
        } catch (OAuthSystemException | OAuthProblemException e) {
          requestContext.abortWith(Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build());
        }
      }
      
      if (!sessionController.isLoggedIn()) {
        requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
      }
    }
  }

}

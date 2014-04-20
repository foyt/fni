package fi.foyt.fni.view.error;

import java.io.FileNotFoundException;
import java.net.URLEncoder;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.el.ELException;
import javax.enterprise.inject.CreationException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.PrettyException;

import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.UnauthorizedException;

@Named
@Stateless
public class ErrorInternalErrorBackingBean {

  public void preRenderListener() throws Throwable {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
    NavigationHandler navigator = facesContext.getApplication().getNavigationHandler();

    Throwable exception = (Throwable) httpServletRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    if (exception != null) {
      Throwable unwrappedException = unwrapExtension(exception);
      if (unwrappedException != null) {
        if (unwrappedException instanceof UnauthorizedException) {
          String requestUri = (String) httpServletRequest.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
          String redirectUrl = "/users/login.jsf?faces-redirect=true";
          
          if (StringUtils.isNotBlank(requestUri)) {
            redirectUrl += "&redirectUrl=" + URLEncoder.encode(requestUri, "UTF-8");
          }
          
          // TODO: faces-redirect=true overrides the status code...
          externalContext.setResponseStatus(HttpServletResponse.SC_UNAUTHORIZED);
          navigator.handleNavigation(facesContext, null, redirectUrl);
        } else if (unwrappedException instanceof FileNotFoundException) {
          navigator.handleNavigation(facesContext, null, "/error/not-found.jsf");
        } else if (unwrappedException instanceof ForbiddenException) {
          navigator.handleNavigation(facesContext, null, "/error/access-denied.jsf");
        }
      }
    }
  }

  private Throwable unwrapExtension(Throwable exception) {
    while ((exception.getCause() != null) && isWrappedException(exception)) {
      exception = exception.getCause();
    }
    
    return exception;
  }

  private boolean isWrappedException(Throwable exception) {
    return exception instanceof ServletException ||
      exception instanceof FacesException || 
      exception instanceof EJBException || 
      exception instanceof ELException || 
      exception instanceof PrettyException || 
      exception instanceof CreationException || 
      exception instanceof IllegalStateException;
  }
  
}

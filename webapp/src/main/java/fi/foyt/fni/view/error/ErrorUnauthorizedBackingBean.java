package fi.foyt.fni.view.error;

import java.io.IOException;
import java.net.URLEncoder;

import javax.ejb.Stateless;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

@Named
@Stateless
public class ErrorUnauthorizedBackingBean {

  public void preRenderListener() throws IOException {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    ExternalContext externalContext = facesContext.getExternalContext();
    HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();

    String forwardUri = (String) httpServletRequest.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
    String redirectUrl = "/users/login.jsf?faces-redirect=true";
    
    if (StringUtils.isNotBlank(forwardUri)) {
      redirectUrl += "&redirectUrl=" + URLEncoder.encode(forwardUri, "UTF-8");
    }
    
    // TODO: faces-redirect=true overrides the status code...
    externalContext.setResponseStatus(HttpServletResponse.SC_UNAUTHORIZED);
    NavigationHandler navigator = facesContext.getApplication().getNavigationHandler();
    navigator.handleNavigation(facesContext, null, redirectUrl);
  }
  
}

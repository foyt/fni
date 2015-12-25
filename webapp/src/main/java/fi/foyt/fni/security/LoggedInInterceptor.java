package fi.foyt.fni.security;

import java.io.Serializable;
import java.net.URLEncoder;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.session.SessionController;

@LoggedIn
@Interceptor
public class LoggedInInterceptor implements Serializable {

	private static final long serialVersionUID = -4809267710739056756L;

	@Inject
	private SessionController sessionController;

  @Inject
	private HttpServletRequest httpServletRequest;
	
	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		if (!sessionController.isLoggedIn()) {
		  FacesContext facesContext = FacesContext.getCurrentInstance();
		  if (facesContext != null) {
		    ExternalContext externalContext = facesContext.getExternalContext();
		    if (!externalContext.isResponseCommitted()) {
  		    HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
          String redirectUrl = (String) httpServletRequest.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
          StringBuilder redirectBuilder = new StringBuilder().append(externalContext.getRequestContextPath()).append("/login/");
  
          if (StringUtils.isNotBlank(redirectUrl)) {
            redirectBuilder.append("?redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8"));
          }
  
          externalContext.redirect(redirectBuilder.toString());
		    }
		    
		    return null;
		  } else {
		    StringBuilder redirectUrlBuilder = new StringBuilder(httpServletRequest.getRequestURI());
	      String queryString = httpServletRequest.getQueryString();
	      if (StringUtils.isNotBlank(queryString)) {
	        redirectUrlBuilder  
	          .append('?')
	          .append(queryString);
	      }
	      
	      return "/users/login.jsf?faces-redirect=true&redirectUrl=" + URLEncoder.encode(redirectUrlBuilder.toString(), "UTF-8");
		  }
		} else {
			return ic.proceed();
		}
	}

}

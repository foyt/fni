package fi.foyt.fni.security;

import java.io.Serializable;

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

	@AroundInvoke
	public Object aroundInvoke(InvocationContext ic) throws Exception {
		if (!sessionController.isLoggedIn()) {
		  FacesContext facesContext = FacesContext.getCurrentInstance();
		  if (facesContext != null) {
		    ExternalContext externalContext = facesContext.getExternalContext();
		    
		    HttpServletRequest httpServletRequest = (HttpServletRequest) externalContext.getRequest();
        String redirectUrl = (String) httpServletRequest.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        StringBuilder redirectBuilder = new StringBuilder().append(externalContext.getRequestContextPath()).append("/login/");

        if (StringUtils.isNotBlank(redirectUrl)) {
          redirectBuilder.append("?redirectUrl=" + redirectUrl);
        }

        externalContext.redirect(redirectBuilder.toString());
		    
		    return null;
		  } else {
  			throw new UnauthorizedException();
		  }
		} else {
			return ic.proceed();
		}
	}

}

package fi.foyt.fni;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.ErrorUtils;

@WebFilter(urlPatterns = "*")
public class ErrorMailerFilter implements Filter {
  
  @Inject
  private SessionController sessionController;
  
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
	    chain.doFilter(request, response);
		} catch (Throwable t) {
		  if (ErrorUtils.isReportableException(t)) {
        String recipient = System.getProperty("fni-error-email");
        if (StringUtils.isNotBlank(recipient)) {
          Long loggedUserId = null;
          if (sessionController.isLoggedIn()) {
            loggedUserId = sessionController.getLoggedUserId();
          }
          
  		    ErrorUtils.mailError(recipient, request, response, t, loggedUserId);
        } else {
          t.printStackTrace();
        }
		  }
      
		  throw t;
		}
	}
  public void init(FilterConfig fConfig) throws ServletException {
    
	}

	@Override
	public void destroy() {
	}
	
}

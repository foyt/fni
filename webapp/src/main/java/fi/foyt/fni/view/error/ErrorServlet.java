package fi.foyt.fni.view.error;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.el.ELException;
import javax.enterprise.inject.CreationException;
import javax.faces.FacesException;
import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.view.AbstractServlet;

@WebServlet(urlPatterns = "/errorHandler")
public class ErrorServlet extends AbstractServlet {

  private static final long serialVersionUID = 8698828812384840114L;
  
  @Inject
  private Logger logger;

  @Inject
  private NavigationController navigationController;

  @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
    if (exception != null) {
      Throwable unwrappedException = unwrapExtension(exception);
      if (unwrappedException != null) {
        if (unwrappedException instanceof UnauthorizedException) {
          String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
          String contextPath = request.getContextPath();
          String redirectUrl = contextPath + "/login/";
          
          if (StringUtils.isNotBlank(requestUri)) {
            redirectUrl += "?redirectUrl=" + requestUri;
          }
          
          sendRedirect(response, response.encodeRedirectURL(redirectUrl));
          return;
        } else if (unwrappedException instanceof FileNotFoundException) {
          forward(request, response, navigationController.notFound());
          return;
        } else if (unwrappedException instanceof ForbiddenException) {
          forward(request, response, navigationController.accessDenied());
          return;
        }
      }
    }
    
    forward(request, response, navigationController.internalError());
	}

  private void forward(HttpServletRequest request, HttpServletResponse response, String path) {
    try {
      request.getRequestDispatcher(path).forward(request, response);
    } catch (ServletException | IOException e) {
      logger.log(Level.SEVERE, "Failed to forward error message", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private Throwable unwrapExtension(Throwable exception) {
    Throwable e = exception;
    
    while ((e.getCause() != null) && isWrappedException(e)) {
      e = e.getCause();
    }
    
    return e;
  }

  private boolean isWrappedException(Throwable exception) {
    return exception instanceof ServletException ||
      exception instanceof FacesException || 
      exception instanceof EJBException || 
      exception instanceof ELException || 
      exception instanceof CreationException || 
      exception instanceof IllegalStateException;
  }
}

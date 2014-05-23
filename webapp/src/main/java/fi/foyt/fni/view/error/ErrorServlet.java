package fi.foyt.fni.view.error;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.EJBException;
import javax.el.ELException;
import javax.enterprise.inject.CreationException;
import javax.faces.FacesException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.UnauthorizedException;

@WebServlet(urlPatterns = "/errorHandler")
public class ErrorServlet extends HttpServlet {

  private static final long serialVersionUID = 8698828812384840114L;

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
          
          response.sendRedirect(response.encodeRedirectURL(redirectUrl));
          return;
        } else if (unwrappedException instanceof FileNotFoundException) {
          request.getRequestDispatcher("/error/not-found.jsf").forward(request, response);
          return;
        } else if (unwrappedException instanceof ForbiddenException) {
          request.getRequestDispatcher("/error/access-denied.jsf").forward(request, response);
          return;
        }
      }
    }
    
    request.getRequestDispatcher("/error/internal-error.jsf").forward(request, response);
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
      exception instanceof CreationException || 
      exception instanceof IllegalStateException;
  }
}

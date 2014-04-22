package fi.foyt.fni.jsf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBException;
import javax.el.ELException;
import javax.enterprise.inject.CreationException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.exception.RewriteException;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.PrettyException;

import fi.foyt.fni.security.ForbiddenException;
import fi.foyt.fni.security.UnauthorizedException;
import fi.foyt.fni.system.ErrorUtils;

public class ExceptionHandler extends ExceptionHandlerWrapper {

  private Logger logger = Logger.getGlobal();

  private final javax.faces.context.ExceptionHandler wrapped;

  public ExceptionHandler(final javax.faces.context.ExceptionHandler wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public javax.faces.context.ExceptionHandler getWrapped() {
    return this.wrapped;
  }

  @Override
  public void handle() throws FacesException {
    for (final Iterator<ExceptionQueuedEvent> queuedEventIterator = getUnhandledExceptionQueuedEvents().iterator(); queuedEventIterator.hasNext();) {
      ExceptionQueuedEvent queuedEvent = queuedEventIterator.next();
      ExceptionQueuedEventContext queuedEventContext = queuedEvent.getContext();

      Throwable exception = queuedEventContext.getException();
      while (isWrappedException(exception) && exception.getCause() != null) {
        exception = exception.getCause();
      }

      FacesContext facesContext = FacesContext.getCurrentInstance();
      ExternalContext externalContext = facesContext.getExternalContext();

      try {
        if (exception instanceof UnauthorizedException) {
          // User tried to perform an operation that required authenticated user
          // but was not logged in.
          // Redirecting user into login form
          try {
            String redirectUrl = null;

            PrettyContext prettyContext = PrettyContext.getCurrentInstance();
            if (prettyContext != null) {
              redirectUrl = new StringBuilder(externalContext.getRequestContextPath()).append(prettyContext.getRequestURL().toString())
                  .append(prettyContext.getRequestQueryString().toQueryString()).toString();
            }

            StringBuilder redirectBuilder = new StringBuilder().append(externalContext.getRequestContextPath()).append("/login/");

            if (StringUtils.isNotBlank(redirectUrl)) {
              redirectBuilder.append("?redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8"));
            }

            externalContext.redirect(redirectBuilder.toString());
          } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred while redirecting to login page", e);
          }
        } else if (exception instanceof ForbiddenException) {
          externalContext.setResponseStatus(HttpServletResponse.SC_FORBIDDEN);
          renderView("/error/access-denied.jsf");
        } else if (exception instanceof FileNotFoundException) {
          externalContext.setResponseStatus(HttpServletResponse.SC_NOT_FOUND);
          renderView("/error/not-found.jsf");
        } else {
          externalContext.setResponseStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          renderView("/error/internal-error.jsf");
          String recipient = System.getProperty("fni-error-email");
          if (ErrorUtils.isReportableException(exception)) {
            if (StringUtils.isNotBlank(recipient) && (externalContext.getRequest() instanceof HttpServletRequest)
                && (externalContext.getResponse() instanceof HttpServletResponse)) {
              ErrorUtils.mailError(recipient, (HttpServletRequest) externalContext.getRequest(), (HttpServletResponse) externalContext.getResponse(),
                  exception, null);
            } else {
              exception.printStackTrace();
            }
          }
        }
      } finally {
        queuedEventIterator.remove();
      }
    }

    getWrapped().handle();
  }

  private boolean isWrappedException(Throwable exception) {
    return 
       exception instanceof FacesException || 
       exception instanceof EJBException || 
       exception instanceof ELException || 
       exception instanceof PrettyException || 
       exception instanceof CreationException || 
       exception instanceof IllegalStateException ||
       exception instanceof RewriteException;
  }

  private void renderView(String viewId) {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    NavigationHandler navigationHandler = facesContext.getApplication().getNavigationHandler();
    navigationHandler.handleNavigation(facesContext, null, viewId);
    facesContext.renderResponse();
  }
}
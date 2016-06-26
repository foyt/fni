package fi.foyt.fni.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.utils.servlet.RequestUtils;

public abstract class AbstractServlet extends HttpServlet {

  private static final long serialVersionUID = 1367225331740547839L;
  
  @Inject
  private Logger logger;
  
  protected void sendRedirect(HttpServletResponse response, String location) {
    response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    response.setHeader("Location", location);
  }
  
  protected void sendError(HttpServletResponse response, int status) {
    sendError(response, status, null);
  }
  
  protected void sendError(HttpServletResponse response, int status, String message) {
    response.setStatus(status);
    if (StringUtils.isNotBlank(message)) {
      PrintWriter writer;
      try {
        writer = response.getWriter();
        writer.write(message);
        writer.flush();
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to send error", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      }
    }
  }

  protected boolean isModifiedSince(HttpServletRequest request, Long lastModified, String eTag) {
    try {
      return RequestUtils.isModifiedSince(request, lastModified, eTag);
    } catch (IOException e) {
      logger.log(Level.FINEST, "IOException occurred when trying to figure out modified since", e);
    }
    
    return true;
  }
}

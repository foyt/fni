package fi.foyt.fni.view.forge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.servlet.RequestUtils;
import fi.foyt.fni.view.AbstractServlet;

@WebServlet(urlPatterns = "/forge/gdrive/*", name = "forge-googledrive")
@Transactional
public class ForgeGoogleDriveServlet extends AbstractServlet {

	private static final long serialVersionUID = -1L;
	private static final long DEFAULT_EXPIRE_TIME = 1000 * 60 * 60;
	
	@Inject
  private Logger logger;

	@Inject
	private MaterialController materialController;
	
	@Inject
	private SessionController sessionController;

	@Inject
  private MaterialPermissionController materialPermissionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		String googleDocumentIdStr = StringUtils.removeStart(pathInfo, "/");
		if (!StringUtils.isNumeric(googleDocumentIdStr)) {
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Invalid request");
			return;
		}
		
		Long googleDocumentId = NumberUtils.createLong(googleDocumentIdStr);
		GoogleDocument googleDocument = materialController.findGoogleDocumentById(googleDocumentId);
		if (googleDocument == null) {
		  sendError(response, HttpServletResponse.SC_NOT_FOUND, "Not Found");
			return;
		}
		
		User loggedUser = sessionController.getLoggedUser();
		if (!materialPermissionController.isPublic(loggedUser, googleDocument)) {
		  if (loggedUser == null) {
		    sendError(response, HttpServletResponse.SC_UNAUTHORIZED);
	      return;
	    }
		  
		  if (!materialPermissionController.hasAccessPermission(loggedUser, googleDocument)) {
		    sendError(response, HttpServletResponse.SC_FORBIDDEN);
        return;
		  }
		}
		
    String eTag = createETag(googleDocument.getModified());
		long lastModified = googleDocument.getModified().getTime();

		try {
      if (!RequestUtils.isModifiedSince(request, lastModified, eTag)) {
        response.setHeader("ETag", eTag); // Required in 304.
        response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        return;
      }
		} catch (IOException e) {
		  logger.log(Level.FINEST, "IOException occurred while resolving while resolving last modified", e);
		}
		
		TypedData data;
		try {
			data = materialController.getGoogleDocumentData(googleDocument);
		} catch (MalformedURLException | GeneralSecurityException e) {
	    logger.log(Level.SEVERE, "Failed to load google document data", e);
	    sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}
		
		if (data == null) {
		  sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error");
			return;
		}

		response.setContentType(data.getContentType());
		response.setHeader("ETag", eTag);
    response.setDateHeader("Last-Modified", lastModified);
    response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);
    
    try {
  		ServletOutputStream outputStream = response.getOutputStream();
  		try {
  			outputStream.write(data.getData());
  		} finally {
  			outputStream.flush();
  		}
    } catch (IOException e) {
      logger.log(Level.FINEST, "IOException occurred on servlet", e);
    }
	}

  private String createETag(Date modified) {
    StringBuilder eTagBuilder = new StringBuilder();
    eTagBuilder.append("W/").append(modified.getTime());
    return eTagBuilder.toString();
  }
}

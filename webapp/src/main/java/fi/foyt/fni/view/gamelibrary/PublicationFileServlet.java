package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationFile;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/gamelibrary/publicationFiles/*", name = "gamelibrary-publicationfile")
@Transactional
public class PublicationFileServlet extends AbstractFileServlet {

	private static final long serialVersionUID = -5117742561225873455L;
	
  @Inject
	private Logger logger;

	@Inject
	private PublicationController publicationController;

	@Inject
	private SessionController sessionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// PublicationId could not be resolved, send 404
		Long publicationId = getPathId(request);
		if (publicationId == null) {
		  sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// BookPublication was not found, send 404
		BookPublication bookPublication = publicationController.findBookPublicationById(publicationId);
		if (bookPublication == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		if (!bookPublication.getPublished()) {
			if (!sessionController.isLoggedIn()) {
	      sendError(response, HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			if (!bookPublication.getCreator().getId().equals(sessionController.getLoggedUserId())) {
  			if (!sessionController.hasLoggedUserPermission(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)) {
          sendError(response, HttpServletResponse.SC_FORBIDDEN);
		  		return;
			  }
			}
		}
		
		// BookPublication does not have a file, send 404
		PublicationFile file = bookPublication.getDownloadableFile();
		if (file == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		publicationController.incBookPublicationDownloadCount(bookPublication);

		String fileName = bookPublication.getUrlName();
		if ("application/pdf".equals(file.getContentType())) {
			fileName += ".pdf";
		}
		
		String eTag = createETag(bookPublication.getModified());
		long lastModified = bookPublication.getModified().getTime();
		response.setHeader("content-disposition", "attachment; filename=" + fileName);

		if (!isModifiedSince(request, lastModified, eTag)) {
			response.setHeader("ETag", eTag); // Required in 304.
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		
		TypedData data = new TypedData(file.getContent(), file.getContentType());

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
		  logger.log(Level.SEVERE, "Failed to send response", e);
		  sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User loggedUser = sessionController.getLoggedUser();
		if (loggedUser == null) {
      sendError(response, HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		if (!sessionController.hasLoggedUserPermission(Permission.GAMELIBRARY_MANAGE_PUBLICATIONS)) {
      sendError(response, HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		// PublicationId could not be resolved, send 404
		Long publicationId = getPathId(request);
		if (publicationId == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// BookPublication was not found, send 404
		BookPublication bookPublication = publicationController.findBookPublicationById(publicationId);
		if (bookPublication == null) {
			sendError(response, HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		FileType fileType = null;
		
		try {
			TypedData file = null;
			List<FileItem> items = getFileItems(request);
			
			for (FileItem item : items) {
				if (!item.isFormField()) {
					if (file != null) {
					  sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Multiple files found from request");
					  return;
					} else {
						file = new TypedData(item.get(), item.getContentType());
					}
				} else {
				  if ("type".equals(item.getFieldName())) {
				    fileType = FileType.valueOf(new String(item.get()));
				  }
				}
			}
      
      if (file == null) {
        sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Missing file");
        return;
      }
      
			if (fileType == null) {
			  sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return;
			}
			
			switch (fileType) {
        case DOWNLOADABLE:
          publicationController.setBookPublicationDownloadableFile(bookPublication, file.getData(), file.getContentType(), loggedUser);
        break;
        case PRINTABLE:
          publicationController.setBookPublicationPrintableFile(bookPublication, file.getData(), file.getContentType(), loggedUser);
        break;
			}

			sendRedirect(response, new StringBuilder()
  		  .append(request.getContextPath())
  		  .append("/gamelibrary/manage/")
  		  .toString()
			);
			
		} catch (FileUploadException e) {
		  logger.log(Level.SEVERE, "File uploading failed", e);
			sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
	}
	
	protected String createETag(Date modified) {
		StringBuilder eTagBuilder = new StringBuilder();
		eTagBuilder.append("W/").append(modified.getTime());
		return eTagBuilder.toString();
	}
	
	private enum FileType {
	  DOWNLOADABLE,
	  PRINTABLE
	}
}
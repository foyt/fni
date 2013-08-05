package fi.foyt.fni.view.gamelibrary;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationFile;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/gamelibrary/publicationFiles/*")
public class PublicationFileServlet extends AbstractFileServlet {

	private static final long serialVersionUID = -5117742561225873455L;

	@Inject
	private ProductController productController;

	@Inject
	private SessionController sessionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ProductId could not be resolved, send 404
		Long productId = getPathId(request);
		if (productId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// FileProduct was not found, send 404
		BookPublication bookPublication = productController.findBookProductById(productId);
		if (bookPublication == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		// FileProduct does not have a file, send 404
		PublicationFile file = bookPublication.getFile();
		if (file == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// TODO: If product is unpublished, only managers may view it

		String eTag = createETag(bookPublication.getModified());
		long lastModified = bookPublication.getModified().getTime();

		if (!isModifiedSince(request, lastModified, eTag)) {
			response.setHeader("ETag", eTag); // Required in 304.
			response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		
		TypedData data = new TypedData(file.getContent(), file.getContentType());

		response.setContentType(data.getContentType());
		response.setHeader("ETag", eTag);
		response.setDateHeader("Last-Modified", lastModified);
		response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(data.getData());
		} finally {
			outputStream.flush();
			outputStream.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO: Security
		
		// ProductId could not be resolved, send 404
		Long productId = getPathId(request);
		if (productId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// FileProduct was not found, send 404
		BookPublication bookPublication = productController.findBookProductById(productId);
		if (bookPublication == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		User loggedUser = sessionController.getLoggedUser();

		try {
			TypedData file = null;
			List<FileItem> items = getFileItems(request);
			
			for (FileItem item : items) {
				if (!item.isFormField()) {
					if (file != null) {
					  throw new ServletException("Multiple files found from request");
					} else {
						file = new TypedData(item.get(), item.getContentType());
					}
				} 
			}
			
			if (bookPublication.getFile() != null) {
				productController.updateBookPublicationFile(bookPublication, file.getContentType(), file.getData(), loggedUser);
			} else {
				productController.createBookPublicationFile(bookPublication, file.getContentType(), file.getData(), loggedUser);
			}
			
		} catch (FileUploadException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
	}
	
	protected String createETag(Date modified) {
		StringBuilder eTagBuilder = new StringBuilder();
		eTagBuilder.append("W/").append(modified.getTime());
		return eTagBuilder.toString();
	}
}
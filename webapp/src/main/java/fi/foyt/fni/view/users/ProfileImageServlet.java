package fi.foyt.fni.view.users;

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
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/users/profileImages/*")
public class ProfileImageServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;

	@Inject
	private UserController userController;

	@Inject
	private SessionController sessionController;

	@Inject
	private MaterialController materialController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ProductImageId could not be resolved, send 404
		Long userId = getPathId(request);
		if (userId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		User user = userController.findUserById(userId);
		if (user == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Image profileImage = user.getProfileImage();
		if (profileImage == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Integer width = NumberUtils.createInteger(request.getParameter("width"));
		Integer height = NumberUtils.createInteger(request.getParameter("height"));
		String eTag = createETag(profileImage.getModified(), width, height);
		long lastModified = profileImage.getModified().getTime();

		if (!isModifiedSince(request, lastModified, eTag)) {
			response.setHeader("ETag", eTag); // Required in 304.
			response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		TypedData data = new TypedData(profileImage.getData(), profileImage.getContentType());

		if ((width != null) && (height != null)) {
			try {
				data = ImageUtils.resizeImage(data, width, height, null);
			} catch (IOException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resize image");
				return;
			}
		}

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
	@LoggedIn
	@Secure(Permission.PROFILE_UPDATE)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// UserId could not be resolved, send 404
		Long userId = getPathId(request);
		if (userId == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		// User was not found, send 404
		User user = userController.findUserById(userId);
		if (user == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		User loggedUser = sessionController.getLoggedUser();

		try {
			String title = null;
			TypedData file = null;
			List<FileItem> items = getFileItems(request);

			for (FileItem item : items) {
				if (!item.isFormField()) {
					if (file != null) {
						throw new ServletException("Multiple files found from request");
					} else {
						file = new TypedData(item.get(), item.getContentType());
						title = item.getName();
					}
				}
			}

			if (user.getProfileImage() != null) {
				materialController.updateImageContent(user.getProfileImage(), file.getContentType(), file.getData(), loggedUser);
			} else {
				materialController.createImage(null, loggedUser, file.getData(), file.getContentType(), title);
			}

		} catch (FileUploadException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
	}

	private String createETag(Date modified, Integer width, Integer height) {
		StringBuilder eTagBuilder = new StringBuilder();

		eTagBuilder.append("W/").append(modified.getTime());

		if (width != null) {
			eTagBuilder.append('-').append(width);
		}

		if (height != null) {
			eTagBuilder.append('-').append(height);
		}

		return eTagBuilder.toString();
	}
}

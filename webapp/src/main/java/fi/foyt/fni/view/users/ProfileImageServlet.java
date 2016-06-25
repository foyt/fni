package fi.foyt.fni.view.users;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/users/profileImages/*", name = "users-profileimage")
@Transactional
public class ProfileImageServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;
	
  private final static String GRAVATAR_URL = "://www.gravatar.com/avatar/";
  
  @Inject
  private Logger logger;

	@Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  Long userId = getPathId(request);
    // user id could not be resolved, send 404
    if (userId == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
	  
	  User user = userController.findUserById(userId);
    if (user == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
		
		Integer width = NumberUtils.createInteger(request.getParameter("width"));
		Integer height = NumberUtils.createInteger(request.getParameter("height"));
		
		if ((width == null)||(height == null)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Width and height parameters are mandatory");
			return;
		}
		
		TypedData profileImage = null;
		UserProfileImageSource profileImageSource = user.getProfileImageSource();
		switch (profileImageSource) {
			case FNI:
				profileImage = userController.getProfileImage(user);
			break;
			case GRAVATAR:
				String protocol = "http";
				if (request.isSecure()) {
					protocol = "https";
				}
				
				String gravatarUrl = getGravatar(protocol, user, Math.max(width, height));
				if (StringUtils.isBlank(gravatarUrl)) {
		      response.sendError(HttpServletResponse.SC_NOT_FOUND);
		      return;
				} else {
  			  response.sendRedirect(gravatarUrl);
	  		  return;
				}
		}
		
    if (profileImage == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

		String eTag = createETag(profileImage.getModified(), width, height);
		long lastModified = profileImage.getModified().getTime();

		if (!isModifiedSince(request, lastModified, eTag)) {
			response.setHeader("ETag", eTag); // Required in 304.
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		try {
			profileImage = ImageUtils.resizeImage(profileImage, width, height, null);
		} catch (IOException e) {
		  logger.log(Level.WARNING, "Failed to resize image", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resize image");
			return;
		}

		response.setContentType(profileImage.getContentType());
		response.setHeader("ETag", eTag);
		response.setDateHeader("Last-Modified", lastModified);
		response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

		ServletOutputStream outputStream = response.getOutputStream();
		try {
			outputStream.write(profileImage.getData());
		} finally {
			outputStream.flush();
		}
	}

	private String getGravatar(String protocol, User user, int size) {
		String email = StringUtils.lowerCase(StringUtils.trim(userController.getUserPrimaryEmail(user)));
		if (StringUtils.isBlank(email)) {
		  return null;
		}
		
		String emailHash = DigestUtils.md5Hex(email);
		
		return new StringBuilder()
		  .append(protocol)
		  .append(GRAVATAR_URL)
		  .append(emailHash)
		  .append(".png")
		  .append("?s=")
		  .append(size)
		  .append("&d=monsterid")
		  .toString();
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
			
			if (file == null) {
			  response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file");
	      return;
			}

			userController.updateProfileImage(loggedUser, file.getContentType(), file.getData());
			userController.updateProfileImageSource(loggedUser, UserProfileImageSource.FNI);
		} catch (FileUploadException e) {
      logger.log(Level.SEVERE, "File uploading failed", e);
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

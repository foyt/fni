package fi.foyt.fni.view.illusion;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/groupAvatar/*", name = "illusion-groupavatar")
public class IllusionGroupAvatarServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;
	
  private final static String GRAVATAR_URL = "://www.gravatar.com/avatar/";

	@Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

  @Inject
	private ChatCredentialsController chatCredentialsController;

  @Inject
  private IllusionGroupController illusionGroupController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String pathInfo = request.getPathInfo();
	  if (StringUtils.isBlank(pathInfo)) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
	    return;
	  }
	  
	  String[] pathItems = StringUtils.removeStart(pathInfo, "/").split("/");
	  if (pathItems.length != 2) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  String groupUrlName = pathItems[0];
	  String jid = pathItems[1];
	  
	  if (StringUtils.isBlank(groupUrlName) || StringUtils.isBlank(jid)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  Integer size = NumberUtils.createInteger(request.getParameter("size"));
    if (size == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Size parameters is mandatory");
      return;
    }
    
	  if (!sessionController.isLoggedIn()) {
	    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
	  }
	  
	  IllusionGroup illusionGroup = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
	  if (illusionGroup == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  // TODO: Check whether user is a participant or not...
  	  
	  UserChatCredentials chatCredentials = chatCredentialsController.findUserChatCredentialsByUserJid(jid);
	  if (chatCredentials == null) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
	    return;
	  }
	  
	  TypedData profileImage = null;
	  User user = chatCredentials.getUser();
    if (user != null) {
      switch (user.getProfileImageSource()) {
        case FNI:
          profileImage = userController.getProfileImage(user);
          if (profileImage == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
          }
        break;
        case GRAVATAR:
          String protocol = "http";
          if (request.isSecure()) {
            protocol = "https";
          }
          
          String gravatarUrl = getGravatar(protocol, user, size);
          response.sendRedirect(gravatarUrl);
          return;
      }
    }
    
	  String eTag = createETag(profileImage.getModified(), size);
	  long lastModified = profileImage.getModified().getTime();

    TypedData imageData = null;
	  if (!isModifiedSince(request, lastModified, eTag)) {
	    response.setHeader("ETag", eTag); // Required in 304.
	    response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
	    return;
	  }
	  
    BufferedImage avatarImage = ImageUtils.readBufferedImage(profileImage);
    if (avatarImage == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
      
    int cropSize = Math.min(avatarImage.getWidth(), avatarImage.getHeight());
    BufferedImage croppedImage = avatarImage.getSubimage(0, 0, cropSize, cropSize);
    BufferedImage resizedImage = ImageUtils.resizeImage(croppedImage, size, size, null);
    
    imageData = ImageUtils.writeBufferedImage(resizedImage);
    
    if (imageData == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }      

    response.setContentType(imageData.getContentType());
    response.setHeader("ETag", eTag);
    response.setDateHeader("Last-Modified", lastModified);
    response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

    ServletOutputStream outputStream = response.getOutputStream();
    try {
      outputStream.write(imageData.getData());
    } finally {
      outputStream.flush();
    }
	}


  private String getGravatar(String protocol, User user, int size) {
		String email = StringUtils.lowerCase(StringUtils.trim(userController.getUserPrimaryEmail(user)));
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

	private String createETag(Date modified, Integer size) {
		StringBuilder eTagBuilder = new StringBuilder();

		eTagBuilder.append("W/").append(modified.getTime());

		if (size != null) {
			eTagBuilder.append('-').append(size);
		}

		return eTagBuilder.toString();
	}
}

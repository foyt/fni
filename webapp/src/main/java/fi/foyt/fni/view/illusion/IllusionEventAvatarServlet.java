package fi.foyt.fni.view.illusion;

import java.awt.image.BufferedImage;
import java.io.IOException;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantImage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/eventAvatar/*", name = "illusion-eventavatar")
@Transactional
public class IllusionEventAvatarServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;
	
  private final static String GRAVATAR_URL = "://www.gravatar.com/avatar/";
  
  @Inject
  private Logger logger;

	@Inject
	private UserController userController;

  @Inject
  private IllusionEventController illusionEventController;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String pathInfo = request.getPathInfo();
	  if (StringUtils.isBlank(pathInfo)) {
	    sendError(response, HttpServletResponse.SC_NOT_FOUND);
	    return;
	  }
	  
	  String[] pathItems = StringUtils.removeStart(pathInfo, "/").split("/");
	  if (pathItems.length != 2) {
	    sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  if (StringUtils.isBlank(pathItems[0]) || !StringUtils.isNumeric(pathItems[1])) {
	    sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  String eventUrlName = pathItems[0];
	  Long participantId = NumberUtils.createLong(pathItems[1]);
	  
	  Integer size = NumberUtils.createInteger(request.getParameter("size"));
    if (size == null) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Size parameters is mandatory");
      return;
    }
	  
	  IllusionEvent illusionEvent = illusionEventController.findIllusionEventByUrlName(eventUrlName);
	  if (illusionEvent == null) {
	    sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
	  if (participant == null) {
	    sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
	  
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(eventUrlName);
    if (event == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    if (!event.getId().equals(participant.getEvent().getId())) {
      sendError(response, HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    
    TypedData profileImage = null;
    
    IllusionEventParticipantImage image = illusionEventController.findIllusionEventParticipantImageByParticipant(participant);
    if (image != null) {
      profileImage = new TypedData(image.getData(), image.getContentType(), image.getModified());
    } else {
      User user = participant.getUser();
      if (user != null) {
        switch (user.getProfileImageSource()) {
          case FNI:
            profileImage = userController.getProfileImage(user);
          break;
          case GRAVATAR:
            String protocol = "http";
            if (request.isSecure()) {
              protocol = "https";
            }
            
            String gravatarUrl = getGravatar(protocol, user, size);
            sendRedirect(response, gravatarUrl);
            return;
        }
      }      
    }
    
    if (profileImage == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
	  String eTag = createETag(profileImage.getModified(), size);
	  long lastModified = profileImage.getModified().getTime();

    TypedData imageData = null;
	  if (!isModifiedSince(request, lastModified, eTag)) {
	    response.setHeader("ETag", eTag); // Required in 304.
	    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	    return;
	  }
	  
	  BufferedImage avatarImage = null;
	  
	  try {
	    avatarImage = ImageUtils.readBufferedImage(profileImage);
	  } catch (IOException e) {
	    logger.log(Level.SEVERE, "Failed to read image", e);
	    sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to read image");
	    return;
	  }
	  
    if (avatarImage == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
      
    int cropSize = Math.min(avatarImage.getWidth(), avatarImage.getHeight());
    BufferedImage croppedImage = avatarImage.getSubimage(0, 0, cropSize, cropSize);
    BufferedImage resizedImage = null;
    
    try {
      resizedImage = ImageUtils.resizeImage(croppedImage, size, size, null);
      imageData = ImageUtils.writeBufferedImage(resizedImage);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to resize image", e);
      sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to resize image");
      return;
    }
    
    if (imageData == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }      

    response.setContentType(imageData.getContentType());
    response.setHeader("ETag", eTag);
    response.setDateHeader("Last-Modified", lastModified);
    response.setDateHeader("Expires", System.currentTimeMillis() + DEFAULT_EXPIRE_TIME);

    try {
      ServletOutputStream outputStream = response.getOutputStream();
      try {
        outputStream.write(imageData.getData());
      } finally {
        outputStream.flush();
      }
    } catch (IOException e) {
      logger.log(Level.FINEST, "IOException occurred on servlet", e);
    }
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String pathInfo = request.getPathInfo();
    if (StringUtils.isBlank(pathInfo)) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String[] pathItems = StringUtils.removeStart(pathInfo, "/").split("/");
    if (pathItems.length != 2) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    if (StringUtils.isBlank(pathItems[0]) || !StringUtils.isNumeric(pathItems[1])) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String eventUrlName = pathItems[0];
    Long participantId = NumberUtils.createLong(pathItems[1]);

    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(eventUrlName);
    if (event == null) {
      sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
	  String dataParam = request.getParameter("data");
	  if (StringUtils.isBlank(dataParam)) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Data parameter is mandatory");
      return;
	  }
	  
	  if (!StringUtils.startsWith(dataParam, "data:")) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Data parameter is invalid");
      return;
	  }
	  
	  String[] dataParts = StringUtils.stripStart(dataParam, "data:").split(";base64,", 2);
	  if (dataParts.length != 2) {
      sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Data parameter is invalid");
      return; 
	  }
	  
	  IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
	  if (participant == null) {
	    sendError(response, HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
    
    String contentType = dataParts[0];
    byte[] data = Base64.decodeBase64(dataParts[1]);
    Date now = new Date();
	  
	  IllusionEventParticipantImage image = illusionEventController.findIllusionEventParticipantImageByParticipant(participant);
	  if (image != null) {
	    illusionEventController.updateIllusionEventParticipantImage(image, contentType, data, now);
	  } else {
	    illusionEventController.createIllusionEeventParticipantImage(participant, contentType, data, now);
	  }
	  
	  response.setStatus(HttpServletResponse.SC_NO_CONTENT);
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

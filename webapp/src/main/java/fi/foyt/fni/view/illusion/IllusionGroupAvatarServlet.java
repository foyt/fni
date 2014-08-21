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
import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantImage;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.data.TypedData;
import fi.foyt.fni.utils.images.ImageUtils;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/groupAvatar/*", name = "illusion-groupavatar")
@Transactional
public class IllusionGroupAvatarServlet extends AbstractFileServlet {

	private static final long serialVersionUID = 8109481247044843102L;
	
  private final static String GRAVATAR_URL = "://www.gravatar.com/avatar/";

	@Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

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
	  
	  if (StringUtils.isBlank(pathItems[0]) || !StringUtils.isNumeric(pathItems[1])) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  String groupUrlName = pathItems[0];
	  Long memberId = NumberUtils.createLong(pathItems[1]);
	  
	  Integer size = NumberUtils.createInteger(request.getParameter("size"));
    if (size == null) {
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Size parameters is mandatory");
      return;
    }
    
	  if (!sessionController.isLoggedIn()) {
	    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
	  }
	  
	  IllusionEvent illusionEvent = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
	  if (illusionEvent == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
	  
	  IllusionEventParticipant member = illusionGroupController.findIllusionGroupMemberById(memberId);
	  if (member == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
	  
    IllusionEvent group = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
    if (group == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    if (!group.getId().equals(member.getGroup().getId())) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    
    TypedData profileImage = null;
    
    IllusionEventParticipantImage image = illusionGroupController.findIllusionGroupMemberImageByMember(member);
    if (image != null) {
      profileImage = new TypedData(image.getData(), image.getContentType(), image.getModified());
    } else {
      User user = member.getUser();
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
            response.sendRedirect(gravatarUrl);
            return;
        }
      }      
    }
    
    if (profileImage == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
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

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    
    if (StringUtils.isBlank(pathItems[0]) || !StringUtils.isNumeric(pathItems[1])) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String groupUrlName = pathItems[0];
    Long memberId = NumberUtils.createLong(pathItems[1]);
    
    if (!sessionController.isLoggedIn()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    IllusionEvent group = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
    if (group == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
	  String dataParam = request.getParameter("data");
	  if (StringUtils.isBlank(dataParam)) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data parameter is mandatory");
      return;
	  }
	  
	  if (!StringUtils.startsWith(dataParam, "data:")) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data parameter is invalid");
      return;
	  }
	  
	  String[] dataParts = StringUtils.stripStart(dataParam, "data:").split(";base64,", 2);
	  if (dataParts.length != 2) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Data parameter is invalid");
      return; 
	  }
	  
	  IllusionEventParticipant member = illusionGroupController.findIllusionGroupMemberById(memberId);
	  if (member == null) {
	    response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
	  }
    
    if (!member.getUser().getId().equals(sessionController.getLoggedUserId())) {
      IllusionEventParticipant loggedParticipant = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, sessionController.getLoggedUser());
      if (loggedParticipant == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
      
      if (loggedParticipant.getRole() != IllusionEventParticipantRole.GAMEMASTER) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
    }
    
    String contentType = dataParts[0];
    byte[] data = Base64.decodeBase64(dataParts[1]);
    Date now = new Date();
	  
	  IllusionEventParticipantImage image = illusionGroupController.findIllusionGroupMemberImageByMember(member);
	  if (image != null) {
	    illusionGroupController.updateIllusionGroupMemberImage(image, contentType, data, now);
	  } else {
	    illusionGroupController.createIllusionGroupMemberImage(member, contentType, data, now);
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

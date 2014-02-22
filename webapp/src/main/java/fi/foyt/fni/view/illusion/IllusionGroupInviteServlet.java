package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.foyt.fni.chat.ChatCredentialsController;
import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/groupInvite/*", name = "illusion-groupinvite")
public class IllusionGroupInviteServlet extends AbstractFileServlet {

  private static final long serialVersionUID = 8840385463120576014L;

  @Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

  @Inject
	private ChatCredentialsController chatCredentialsController;

  @Inject
  private IllusionGroupController illusionGroupController;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  String pathInfo = request.getPathInfo();
    if (StringUtils.isBlank(pathInfo)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String[] pathItems = StringUtils.removeStart(pathInfo, "/").split("/");
    if (pathItems.length != 1) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    String groupUrlName = pathItems[0];
    if (StringUtils.isBlank(groupUrlName)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    if (!sessionController.isLoggedIn()) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    User loggedUser = sessionController.getLoggedUser();
    IllusionGroup group = illusionGroupController.findIllusionGroupByUrlName(groupUrlName);
    if (group == null) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    
    IllusionGroupUser loggedGroupUser = illusionGroupController.findIllusionGroupUserByUserAndGroup(group, loggedUser);
    if ((loggedGroupUser == null)||(loggedGroupUser.getRole() != IllusionGroupUserRole.GAMEMASTER)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }
    
    List<String> inviteJids = new ArrayList<>();

    String[] userIds = request.getParameterValues("userId");
    for (String userId : userIds) {
      if (StringUtils.isNumeric(userId)) {
        User user = userController.findUserById(NumberUtils.createLong(userId));
        if (user == null) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
        }
        
        UserChatCredentials userChatCredentials = chatCredentialsController.findUserChatCredentialsByUser(user);
        inviteJids.add(userChatCredentials.getUserJid());
        
        IllusionGroupUser illusionGroupUser = illusionGroupController.findIllusionGroupUserByUserAndGroup(group, user);
        if (illusionGroupUser == null) {
          illusionGroupController.createIllusionGroupUser(user, group, getUserNickname(user), IllusionGroupUserRole.PLAYER);
        }
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }
    
    response.setContentType("application/json");
    ObjectMapper objectMapper = new ObjectMapper();
    ServletOutputStream outputStream = response.getOutputStream();
    try {
      objectMapper.writeValue(outputStream, new ResponseJson(inviteJids));
    } finally {
      outputStream.flush();
    }
    
	  response.setStatus(HttpServletResponse.SC_OK);
	}

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }

  private static class ResponseJson {
    
    public ResponseJson(List<String> inviteJids) {
      this.inviteJids = inviteJids;
    }
    
    @SuppressWarnings("unused")
    public List<String> getInviteJids() {
      return inviteJids;
    }
    
    private List<String> inviteJids;
  }
  
}

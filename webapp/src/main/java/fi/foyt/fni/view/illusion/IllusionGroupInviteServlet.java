package fi.foyt.fni.view.illusion;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.AbstractFileServlet;

@WebServlet(urlPatterns = "/illusion/groupInvite/*", name = "illusion-groupinvite")
@Transactional
public class IllusionGroupInviteServlet extends AbstractFileServlet {

  private static final long serialVersionUID = 8840385463120576014L;

  @Inject
	private UserController userController;

  @Inject
	private SessionController sessionController;

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
    
    IllusionGroupMember loggedGroupUser = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, loggedUser);
    if ((loggedGroupUser == null)||(loggedGroupUser.getRole() != IllusionGroupMemberRole.GAMEMASTER)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN);
      return;
    }

    String[] userIds = request.getParameterValues("userId");
    for (String userId : userIds) {
      if (StringUtils.isNumeric(userId)) {
        User user = userController.findUserById(NumberUtils.createLong(userId));
        if (user == null) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
        }
        
        IllusionGroupMember illusionGroupUser = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, user);
        if (illusionGroupUser == null) {
          illusionGroupController.createIllusionGroupMember(user, group, getUserNickname(user), IllusionGroupMemberRole.PLAYER);
        }
      } else {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        return;
      }
    }

	  response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }

}

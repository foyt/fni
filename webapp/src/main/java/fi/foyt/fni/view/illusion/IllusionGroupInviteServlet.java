package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.illusion.IllusionGroupController;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
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

  @Inject
  private SystemSettingsController systemSettingsController;

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

    Locale defaultLocale = systemSettingsController.getDefaultLocale();
    Date now = new Date();
    
    String[] emails = request.getParameterValues("email");
    for (String email : emails) {
      User user = userController.findUserByEmail(email);
      if (user == null) {
        user = userController.createUser(null, null, null, defaultLocale, now, UserProfileImageSource.GRAVATAR);
        userController.createUserEmail(user, email, Boolean.TRUE);
      }
      
      IllusionGroupMember illusionGroupUser = illusionGroupController.findIllusionGroupMemberByUserAndGroup(group, user);
      if (illusionGroupUser == null) {
        illusionGroupController.createIllusionGroupMember(user, group, getUserNickname(user), IllusionGroupMemberRole.INVITED);
      }
    }

	  response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

  private String getUserNickname(User user) {
    return StringUtils.isNotBlank(user.getNickname()) ? user.getNickname() : user.getFullName();
  }

}

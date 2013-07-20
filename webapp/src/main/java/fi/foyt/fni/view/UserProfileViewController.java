package fi.foyt.fni.view;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserFriendDAO;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Stateful
public class UserProfileViewController extends PageViewController {

  @Inject
  private SessionController sessionController;

	@Inject
	private UserFriendDAO userFriendDAO;

	@Inject
	private UserDAO userDAO;
	
	@Override
	public boolean checkPermissions(ViewControllerContext context) {
	  return sessionController.isLoggedIn();
	}
	
	@Override
	public void execute(ViewControllerContext context) {
		super.execute(context);

		Long userId = context.getLongParameter("userId");
    if (userId == null) {
    	throw new ViewControllerException(Locales.getText(context.getRequest().getLocale(), "generic.error.missingParameter", "userId"));
    }
		
		User user = userDAO.findById(userId);
		
		List<User> friends = userFriendDAO.listFriendUsersByConfirmed(user, Boolean.TRUE);
		
		context.getRequest().setAttribute("user", user);
		context.getRequest().setAttribute("friends", friends);
		context.setIncludeJSP("/jsp/userprofile.jsp");
	}
	
}

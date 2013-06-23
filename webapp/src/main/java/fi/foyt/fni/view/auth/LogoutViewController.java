package fi.foyt.fni.view.auth;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.view.AbstractViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class LogoutViewController extends AbstractViewController {
	
  @Inject
  private SessionController sessionController;
  
	@Inject
	@DAO
	private UserTokenDAO userTokenDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
    context.setRedirect(context.getRequest().getContextPath() + "/", false);

    HttpSession session = context.getRequest().getSession();
    session.invalidate();
    sessionController.logout();
  }

}
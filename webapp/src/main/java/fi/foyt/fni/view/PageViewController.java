package fi.foyt.fni.view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.messages.MessageController;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
public abstract class PageViewController extends AbstractViewController {

	@Inject
	private SystemSettingsController systemSettingsController;

  @Inject
  private SessionController sessionController;

  @Inject
  private MessageController messageController;

	@Override
	public void execute(ViewControllerContext context) {
  	User loggedUser = sessionController.getLoggedUser();
  	
    if (loggedUser != null) {
      context.getRequest().setAttribute("loggedIn", true);
      context.getRequest().setAttribute("loggedUser", loggedUser);
      context.getRequest().setAttribute("newMessages", messageController.getNewMessageCount(loggedUser));
    }
    
    context.getRequest().setAttribute("theme", systemSettingsController.getTheme());
	}
	
}

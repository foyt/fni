package fi.foyt.fni.view.forum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import fi.foyt.fni.persistence.dao.forum.ForumCategoryDAO;
import fi.foyt.fni.persistence.dao.forum.ForumDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.view.PageViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class ForumIndexViewController extends PageViewController {

  @Inject
  private SessionController sessionController;
  
	@Inject
	private ForumCategoryDAO forumCategoryDAO;
	
	@Inject
	private ForumDAO forumDAO;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
  	super.execute(context);
  	
    List<ForumCategory> forumCategories = forumCategoryDAO.listAll();
    Map<ForumCategory, List<Forum>> forums = new HashMap<ForumCategory, List<Forum>>();
    
    for (ForumCategory forumCategory : forumCategories) {
      List<Forum> categoryForums = forumDAO.listByCategory(forumCategory);
      forums.put(forumCategory, categoryForums);
    }
    
    User loggedUser = sessionController.getLoggedUser();

    context.getRequest().setAttribute("canModerate", AuthUtils.getInstance().isAllowed(loggedUser, UserRole.ADMINISTRATOR));
    context.getRequest().setAttribute("categories", forumCategories);
    context.getRequest().setAttribute("forums", forums);
    
    context.setIncludeJSP("/jsp/forum/index.jsp");
  }

  
}
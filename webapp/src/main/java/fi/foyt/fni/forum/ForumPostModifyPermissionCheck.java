package fi.foyt.fni.forum;

import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;
import fi.foyt.fni.session.SessionController;

@Stateless
@PermissionCheck (Permission.FORUM_POST_MODIFY)
public class ForumPostModifyPermissionCheck implements PermissionCheckImplementation<Long> {

  @Inject
  private SessionController sessionController;
  
  @Inject
  private ForumController forumController;
  
	@Override
	public boolean checkPermission(Long postId, Map<String, String> parameters) {
	  if (sessionController.isLoggedIn()) {
      if (sessionController.hasLoggedUserPermission(Permission.FORUM_POST_MODERATE)) {
        return true;
      }
      
      ForumPost forumPost = forumController.findForumPostById(postId);
      if (forumPost == null) {
        throw new SecurityException("Could not resolve forum while checking permission for FORUM_TOPIC_MODIFY");
      }
      
      return sessionController.hasLoggedUserPermission(Permission.FORUM_POST_MODIFY) && 
        sessionController.getLoggedUserId().equals(forumPost.getAuthor().getId());
    }
    
    return false;
	}

}

package fi.foyt.fni.forum;

import javax.ejb.Stateless;

import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.PermissionCheck;
import fi.foyt.fni.security.PermissionCheckImplementation;
import fi.foyt.fni.security.SecurityException;

@Stateless
@PermissionCheck (Permission.FORUM_TOPIC_CREATE)
public class ForumTopicCreatePermissionCheck implements PermissionCheckImplementation<Forum> {

	@Override
	public boolean checkPermission(Forum forum) {
		if (forum == null) {
			throw new SecurityException("Could not resolve forum while checking permission for FORUM_TOPIC_CREATE");
		}
		
		return forum.getAllowTopicCreation();
	}

}

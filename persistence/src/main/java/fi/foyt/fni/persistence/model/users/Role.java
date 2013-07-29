package fi.foyt.fni.persistence.model.users;

public enum Role {

	/* Forum */
	
	FORUM_ADMIN (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE),
	FORUM_USER (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE),
	FORUM_GUEST ();
  
	Role(Permission... permissions) {
    this.permissions = permissions;
  }
	
	public Permission[] getPermissions() {
		return permissions;
	}
	
  private Permission[] permissions;
}

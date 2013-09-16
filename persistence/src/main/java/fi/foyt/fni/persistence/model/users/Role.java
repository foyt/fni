package fi.foyt.fni.persistence.model.users;

public enum Role {
	
	/* Guest */
	
	GUEST,
	
	/* User */
	
	USER (Permission.PROFILE_UPDATE),

	/* Forum */
	
	FORUM_ADMIN (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE),
	FORUM_USER (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE),
	
	/* Game Library */
	
	GAME_LIBRARY_USER (Permission.GAMELIBRARY_VIEW_ORDER),
	GAME_LIBRARY_MANAGER (Permission.GAMELIBRARY_VIEW_ORDER, Permission.GAMELIBRARY_MANAGE_PUBLICATIONS);
  
	Role(Permission... permissions) {
    this.permissions = permissions;
  }
	
	public Permission[] getPermissions() {
		return permissions;
	}
	
  private Permission[] permissions;
}

package fi.foyt.fni.persistence.model.users;

public enum Role {
  
  ANONYMOUS (Permission.GAMELIBRARY_VIEW_ORDER),
	
	/* Guest */
	
	GUEST,
	
	/* User */
	
	USER (Permission.PROFILE_UPDATE),
	
	/* Forge */
	
	MATERIAL_USER (Permission.MATERIAL_ACCESS, Permission.MATERIAL_MODIFY, Permission.MATERIAL_DELETE),
	
	/* Illusion */
	
	ILLUSION_USER (Permission.ILLUSION_EVENT_ACCESS),
  ILLUSION_MANAGER (Permission.ILLUSION_EVENT_ACCESS, Permission.ILLUSION_EVENT_MANAGE),

	/* Forum */
	
	FORUM_ADMIN (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE, Permission.FORUM_POST_MODIFY, Permission.FORUM_TOPIC_MODIFY, Permission.FORUM_POST_MODERATE, Permission.FORUM_TOPIC_MODERATE),
  FORUM_MODERATOR (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE, Permission.FORUM_POST_MODIFY, Permission.FORUM_TOPIC_MODIFY, Permission.FORUM_POST_MODERATE, Permission.FORUM_TOPIC_MODERATE),
	FORUM_USER (Permission.FORUM_TOPIC_CREATE, Permission.FORUM_POST_CREATE, Permission.FORUM_POST_MODIFY, Permission.FORUM_TOPIC_MODIFY),
	
	/* Game Library */
	
	GAME_LIBRARY_USER (Permission.GAMELIBRARY_VIEW_ORDER),
	GAME_LIBRARY_MANAGER (Permission.GAMELIBRARY_VIEW_ORDER, Permission.GAMELIBRARY_MANAGE_ORDERS, Permission.GAMELIBRARY_MANAGE_PUBLICATIONS),
	
	SYSTEM_ADMINISTRATOR (Permission.SYSTEM_ADMINISTRATION);
  
	Role(Permission... permissions) {
    this.permissions = permissions;
  }
	
	public Permission[] getPermissions() {
		return permissions;
	}
	
  private Permission[] permissions;
}

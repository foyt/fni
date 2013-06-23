package fi.foyt.fni.api.events;

import javax.ws.rs.core.UriInfo;

import fi.foyt.fni.persistence.model.users.User;

public class FriendRemovedEvent extends ApiEvent {
	
	public FriendRemovedEvent(UriInfo uriInfo, User user, User friend) {
		super(uriInfo);
		this.friend = friend;
		this.user = user;
	}

	public User getFriend() {
		return friend;
	}
	
	public User getUser() {
		return user;
	}
	
	private User user;
	private User friend;
}

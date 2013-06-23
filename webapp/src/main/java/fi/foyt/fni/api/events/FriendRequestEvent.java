package fi.foyt.fni.api.events;

import javax.ws.rs.core.UriInfo;

import fi.foyt.fni.persistence.model.users.User;

public class FriendRequestEvent extends ApiEvent {
	
	public FriendRequestEvent(UriInfo uriInfo, User user, User friend, String confirmKey) {
		super(uriInfo);
		this.friend = friend;
		this.user = user;
		this.confirmKey = confirmKey;
	}

	public User getFriend() {
		return friend;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getConfirmKey() {
		return confirmKey;
	}
	
	private User user;
	private User friend;
	private String confirmKey;
}

package fi.foyt.fni.session;

public class UserSessionEvent {
	
	public UserSessionEvent(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return this.userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	private Long userId;
}

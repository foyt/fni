package fi.foyt.fni.persistence.model.users;

public class CommonFriend {

	public CommonFriend(Long commonFriendCount, User user) {
		this.commonFriendCount = commonFriendCount;
		this.user = user;
	}

	public Long getCommonFriendCount() {
	  return commonFriendCount;
  }
	
	public User getUser() {
		return user;
	}

	private Long commonFriendCount;
	private User user;
}
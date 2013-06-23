package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.users.UserFriend;

public class CompactUserFriendBean {
	
	public CompactUserFriendBean(Long id, CompactUserBean user, CompactUserBean friend, Boolean confirmed) {
	  this.id = id;
	  this.user = user;
	  this.friend = friend;
	  this.confirmed = confirmed;
  }

	public Long getId() {
	  return id;
  }
	
	public void setId(Long id) {
	  this.id = id;
  }
	
	public CompactUserBean getUser() {
	  return user;
  }
	
	public void setUser(CompactUserBean user) {
	  this.user = user;
  }
	
	public CompactUserBean getFriend() {
	  return friend;
  }
	
	public void setFriend(CompactUserBean friend) {
	  this.friend = friend;
  }
	
	public Boolean getConfirmed() {
	  return confirmed;
  }
	
	public void setConfirmed(Boolean confirmed) {
	  this.confirmed = confirmed;
  }
	
	public static CompactUserFriendBean fromEntity(UserFriend entity) {
		if (entity == null)
			return null;
		
		return new CompactUserFriendBean(entity.getId(), CompactUserBean.fromEntity(entity.getUser()), CompactUserBean.fromEntity(entity.getFriend()), entity.getConfirmed());
	}
	
	public static List<CompactUserFriendBean> fromEntities(List<UserFriend> documents) {
		List<CompactUserFriendBean> beans = new ArrayList<CompactUserFriendBean>(documents.size());
		
		for (UserFriend document : documents) {
			beans.add(fromEntity(document));
		}
		
		return beans;
	}
	
	private Long id;

	private CompactUserBean user;
	
	private CompactUserBean friend;
	
	private Boolean confirmed;
}

package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.users.CommonFriend;

public class CompleteCommonFriendBean {

	public CompleteCommonFriendBean(Long commonFriendCount, CompactUserBean user) {
		this.commonFriendCount = commonFriendCount;
		this.user = user;
	}

	public CompactUserBean getUser() {
		return user;
	}
	
	public Long getCommonFriendCount() {
	  return commonFriendCount;
  }

	public static CompleteCommonFriendBean fromEntity(CommonFriend entity) {
		if (entity == null)
			return null;

		return new CompleteCommonFriendBean(entity.getCommonFriendCount(), CompactUserBean.fromEntity(entity.getUser()));
	}

	public static List<CompleteCommonFriendBean> fromEntities(List<CommonFriend> users) {
		List<CompleteCommonFriendBean> beans = new ArrayList<CompleteCommonFriendBean>(users.size());

		for (CommonFriend user : users) {
			beans.add(fromEntity(user));
		}

		return beans;
	}

	private Long commonFriendCount;
	private CompactUserBean user;
}
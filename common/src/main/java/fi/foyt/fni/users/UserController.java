package fi.foyt.fni.users;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserEmailDAO;
import fi.foyt.fni.persistence.dao.users.UserFriendDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserEmail;
import fi.foyt.fni.persistence.model.users.UserFriend;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserSetting;
import fi.foyt.fni.persistence.model.users.UserSettingKey;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;

@RequestScoped
@Stateful
public class UserController {
	
	@Inject
	private UserDAO userDAO;

	@Inject
	private UserEmailDAO userEmailDAO;
	
	@Inject
	private UserIdentifierDAO userIdentifierDAO;
	
	@Inject
	private UserTokenDAO userTokenDAO;
	
	@Inject
	private UserFriendDAO userFriendDAO; 
	
	@Inject
	private UserSettingDAO userSettingDAO;
	
	@Inject
	private UserSettingKeyDAO userSettingKeyDAO;
	
  public User createUser(String firstName, String lastName, String nickname, Locale locale, Date registrationDate) {
    User user = userDAO.create(firstName, lastName, nickname, locale, registrationDate, UserRole.USER);
    return user;
  }

	public User findUserById(Long userId) {
		return userDAO.findById(userId);
	}

  public User findUserByEmail(String email) {
    UserEmail userEmail = userEmailDAO.findByEmail(email);
    if (userEmail != null)
      return userEmail.getUser();
    return null;
  }
  
  public String getUserPrimaryEmail(User user) {
  	UserEmail userEmail = userEmailDAO.findByUserAndPrimary(user, Boolean.TRUE);
  	if (userEmail != null)
  		return userEmail.getEmail();
  	
  	return null;
  }
  
  public User findUserByIdentifier(AuthSource authSource, String identifier) {
    UserIdentifier userIdentifier = userIdentifierDAO.findByAuthSourceAndIdentifier(authSource, identifier);
    if (userIdentifier != null)
      return userIdentifier.getUser();
    return null;
  }
  
  @LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public User updateFirstName(User user, String firstName) {
		return userDAO.updateFirstName(user, firstName);
	}
  
  @LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public User updateLastName(User user, String lastName) {
		return userDAO.updateLastName(user, lastName);
	}
  
  @LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public User updateNickname(User user, String nickname) {
		return userDAO.updateNickname(user, nickname);
	}
  
  public UserFriend getUserFriendByUsers(User user1, User user2) {
    UserFriend userFriend = userFriendDAO.findByUserAndFriend(user1, user2);
    if (userFriend != null)
      return userFriend;

    return userFriendDAO.findByUserAndFriend(user2, user1);
  }

  public List<User> listUserFriends(User user) {
    return listUserFriendsAndConfirmed(user, Boolean.TRUE);
  }

  public List<User> listUserFriendsAndConfirmed(User user, Boolean confirmed) {
    return userFriendDAO.listFriendUsersByConfirmed(user, confirmed);
  }

  public List<UserFriend> listUserFriendsByUser(User user) {
    return userFriendDAO.listByUser(user);
  }

  public List<UserFriend> listUserFriendsByFriend(User friend) {
    return userFriendDAO.listByFriend(friend);
  }

  public List<UserFriend> listUserFriendsByUserAndConfirmed(User user, Boolean confirmed) {
    return userFriendDAO.listByUserAndConfirmed(user, confirmed);
  }

  public List<UserFriend> listUserFriendsByFriendAndConfirmed(User friend, Boolean confirmed) {
    return userFriendDAO.listByFriendAndConfirmed(friend, confirmed);
  }

  public boolean areFriends(User user1, User user2) {
    UserFriend userFriend = userFriendDAO.findByUserAndFriend(user1, user2);
    if (userFriend != null && userFriend.getConfirmed())
    	return true;
    
    userFriend = userFriendDAO.findByUserAndFriend(user2, user1);
    if (userFriend != null && userFriend.getConfirmed())
    	return true;
    
    return false;
  }
  
  public UserSetting getUserSetting(User user, String setting) {
  	UserSettingKey settingKey = userSettingKeyDAO.findByKey(setting);
		if (settingKey != null) {
			UserSetting userSetting = userSettingDAO.findByUserAndUserSettingKey(user, settingKey);
			if (userSetting != null) {
				return userSetting;
			}
		}
		
		return null;
  }

	public String getUserSettingValue(User user, String setting) {
		UserSetting userSetting = getUserSetting(user, setting);
		if (userSetting != null) {
			return userSetting.getValue();
		}
		
		return null;
  }

	public void setUserSettingValue(User user, String setting, String value) {
		UserSetting userSetting = getUserSetting(user, setting);
		if (userSetting != null) {
			userSettingDAO.updateValue(userSetting, value);
		} else {
			UserSettingKey userSettingKey = userSettingKeyDAO.findByKey(setting);
			if (userSettingKey != null) {
			  userSettingDAO.create(user, userSettingKey, value);
			}
		}
	}
}

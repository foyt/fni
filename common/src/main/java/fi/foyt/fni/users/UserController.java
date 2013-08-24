package fi.foyt.fni.users;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.users.AddressDAO;
import fi.foyt.fni.persistence.dao.users.UserContactFieldDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserEmailDAO;
import fi.foyt.fni.persistence.dao.users.UserFriendDAO;
import fi.foyt.fni.persistence.dao.users.UserImageDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.AddressType;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactField;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.persistence.model.users.UserEmail;
import fi.foyt.fni.persistence.model.users.UserFriend;
import fi.foyt.fni.persistence.model.users.UserImage;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserSetting;
import fi.foyt.fni.persistence.model.users.UserSettingKey;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.utils.data.TypedData;

@Dependent
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

	@Inject
	private UserContactFieldDAO userContactFieldDAO;

	@Inject
	private UserImageDAO userImageDAO;

	@Inject
	private AddressDAO addressDAO;
	
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

	public List<User> listUsers() {
		return userDAO.listByArchived(Boolean.FALSE);
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

  @LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public User updateAbout(User user, String about) {
  	return userDAO.updateAbout(user, about);
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
	
	/* Contact Info */

	public UserContactField createContactField(User user, UserContactFieldType type, String value) {
		return userContactFieldDAO.create(user, type, value);
	}

	public UserContactField findContactFieldByType(User user, UserContactFieldType type) {
		return userContactFieldDAO.findByUserAndType(user, type);
	}

	public List<UserContactField> listContactFields(User user) {
		return userContactFieldDAO.listByUser(user);
	}
	
	public UserContactField updateContactField(UserContactField contactField, String value) {
		return userContactFieldDAO.updateValue(contactField, value);
	}

	public void deleteContactField(UserContactField contactField) {
		userContactFieldDAO.delete(contactField);
	}
	
	public String getContactFieldValue(User user, UserContactFieldType type) {
		UserContactField contectInfoField = findContactFieldByType(user, type);
		return contectInfoField != null ? contectInfoField.getValue() : null;
	}

	public UserContactField setContactFieldValue(User user, UserContactFieldType type, String value) {
		UserContactField contactField = findContactFieldByType(user, type);
		if (contactField == null) {
			if (StringUtils.isBlank(value)) {
				return null;
			} else {
			  return createContactField(user, type, value);
			}
		} else {
			if (StringUtils.isBlank(value)) {
			  deleteContactField(contactField);
			  return null;
			} else {			
		  	return updateContactField(contactField, value);
			}
		}
	}

	/* Profile Image */
	
	public TypedData getProfileImage(User user) {
		UserImage userImage = userImageDAO.findByUser(user);
		if (userImage != null) {
			return new TypedData(userImage.getData(), userImage.getContentType(), userImage.getModified());
		}
		
		return null;
	}
	
	public boolean hasProfileImage(User user) {
		return userImageDAO.findByUser(user) != null;
	}
	
  @LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public void updateProfileImage(User user, String contentType, byte[] data) {
		UserImage userImage = userImageDAO.findByUser(user);
		Date now = new Date();
		if (userImage != null) {
			userImageDAO.updateData(userImage, data, now);
			userImageDAO.updateContentType(userImage, contentType, now);
		} else {
			userImageDAO.create(user, contentType, data, now);
		}
	}

  @LoggedIn
	@Secure (Permission.PROFILE_UPDATE)
	public void updateProfileImageSource(User user, UserProfileImageSource profileImageSource) {
		userDAO.updateProfileImageSource(user, profileImageSource);
	}

  /* Address */
  
  public Address createAddress(User user, AddressType addressType, String companyName, String street1, String street2, String postalCode, String city, Country country) {
  	return addressDAO.create(user, addressType, companyName, street1, street2, postalCode, city, country);
  }
  
	public Address findAddressByUserAndType(User user, AddressType addressType) {
		return addressDAO.findByUserAndAddressType(user, addressType);
	}

}

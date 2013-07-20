package fi.foyt.fni.auth;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.auth.InternalAuthDAO;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.users.PasswordResetKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserConfirmKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.users.PasswordResetKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserConfirmKey;
import fi.foyt.fni.persistence.model.users.UserToken;

@RequestScoped
@Stateful
public class AuthenticationController {
	
	@Inject
	private InternalAuthDAO internalAuthDAO;

	@Inject
	private PasswordResetKeyDAO passwordResetKeyDAO;

	@Inject
	private UserConfirmKeyDAO userConfirmKeyDAO;
	
	@Inject
	private UserIdentifierDAO userIdentifierDAO;
	
	@Inject
	private UserTokenDAO userTokenDAO;
	
	// InternalAuth
	
	public InternalAuth findInternalAuthByUser(User user) {
		return internalAuthDAO.findByUser(user);
	}

	public void verifyInternalAuth(UserConfirmKey confirmKey, InternalAuth internalAuth) {
		internalAuthDAO.updateVerified(internalAuth, Boolean.TRUE);
		userConfirmKeyDAO.delete(confirmKey);
  }
	
	public void setUserPassword(User user, String encodedPassword) {
		InternalAuth internalAuth = internalAuthDAO.findByUser(user);
		if (internalAuth == null) {
			internalAuthDAO.create(user, encodedPassword, Boolean.TRUE);
		} else {
			internalAuthDAO.updatePassword(internalAuth, encodedPassword);
		}
	}
	
	// PasswordResetKey
	
	public PasswordResetKey generatePasswordResetKey(User user) {
		return passwordResetKeyDAO.create(user, UUID.randomUUID().toString());
	}
	
	public PasswordResetKey findPasswordResetKey(String key) {
		return passwordResetKeyDAO.findByValue(key);
	}
	
	public void deletePasswordResetKey(PasswordResetKey passwordResetKey) {
		passwordResetKeyDAO.delete(passwordResetKey);
	}

	// UserConfirmKey

	public UserConfirmKey findConfirmKeyByKey(String key) {
		return userConfirmKeyDAO.findByValue(key);
	}

	// UserIdentifier 

	public List<UserIdentifier> listUserIdentifiers(User user) {
		return userIdentifierDAO.listByUser(user);
	}
	
	public void removeUserIdentifier(UserIdentifier userIdentifier) {
		// TODO: Implement rest of the strategies
		switch (userIdentifier.getAuthSource()) {
			case INTERNAL:
				InternalAuth internalAuth = internalAuthDAO.findByUser(userIdentifier.getUser());
				if (internalAuth != null) {
				  internalAuthDAO.delete(internalAuth);
				}
		  break;
		  default:
		  	throw new RuntimeException("Not implemented");
		}
		
		List<UserToken> tokens = userTokenDAO.listByUserIdentifier(userIdentifier);
		for (UserToken token : tokens) {
			userTokenDAO.delete(token);
		}
		
		userIdentifierDAO.delete(userIdentifier);
	}
}

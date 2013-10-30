package fi.foyt.fni.auth;

import java.util.List;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.auth.InternalAuthDAO;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.users.PasswordResetKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserVerificationKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.users.PasswordResetKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserVerificationKey;
import fi.foyt.fni.persistence.model.users.UserToken;

@RequestScoped
@Stateful
public class AuthenticationController {
	
	@Inject
	private InternalAuthDAO internalAuthDAO;

	@Inject
	private PasswordResetKeyDAO passwordResetKeyDAO;

	@Inject
	private UserVerificationKeyDAO userVerificationKeyDAO;
	
	@Inject
	private UserIdentifierDAO userIdentifierDAO;
	
	@Inject
	private UserTokenDAO userTokenDAO;
	
	// InternalAuth
	
	public InternalAuth createInternalAuth(User user, String password) {
		InternalAuth internalAuth = internalAuthDAO.create(user, password, Boolean.FALSE);
	  return internalAuth;
	}	
	
	public InternalAuth findInternalAuthByUser(User user) {
		return internalAuthDAO.findByUser(user);
	}

	public void verifyInternalAuth(UserVerificationKey verificationKey, InternalAuth internalAuth) {
		internalAuthDAO.updateVerified(internalAuth, Boolean.TRUE);
		userVerificationKeyDAO.delete(verificationKey);
  }

	public void verifyInternalAuth(InternalAuth internalAuth) {
		internalAuthDAO.updateVerified(internalAuth, Boolean.TRUE);
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

	// UserVerificationKey

	public UserVerificationKey createVerificationKey(User user, String email) {
		userIdentifierDAO.create(user, AuthSource.INTERNAL, email, "INTERNAL-" + user.getId());	
	  return userVerificationKeyDAO.create(user, UUID.randomUUID().toString());
	}

	public UserVerificationKey findVerificationKey(String key) {
		return userVerificationKeyDAO.findByValue(key);
	}

	// UserIdentifier 

	public UserIdentifier findUserIdentifierById(Long userIdentifierId) {
		return userIdentifierDAO.findById(userIdentifierId);
	}
	
	public List<UserIdentifier> listUserIdentifiers(User user) {
		return userIdentifierDAO.listByUser(user);
	}
	
	public void removeUserIdentifier(UserIdentifier userIdentifier) {
		switch (userIdentifier.getAuthSource()) {
			case INTERNAL:
				InternalAuth internalAuth = internalAuthDAO.findByUser(userIdentifier.getUser());
				if (internalAuth != null) {
				  internalAuthDAO.delete(internalAuth);
				}
		  break;
			default:
			break;
		}
		
		List<UserToken> tokens = userTokenDAO.listByUserIdentifier(userIdentifier);
		for (UserToken token : tokens) {
			userTokenDAO.delete(token);
		}
		
		userIdentifierDAO.delete(userIdentifier);
	}
}

package fi.foyt.fni.auth;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.auth.InternalAuthDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.users.UserController;

public abstract class AbstractInternalAuthenticationStrategy extends AuthenticationStrategy {

	@Inject
	private UserController userController;

	@Inject
	private InternalAuthDAO internalAuthDAO;
	
	protected UserToken handleLogin(Locale locale, String username, String password) throws InvalidCredentialsException, UserNotConfirmedException, MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, InvalidCredentialsException, UserNotConfirmedException {
	  User user = userController.findUserByEmail(username);
		if (user == null) {
			throw new InvalidCredentialsException();
		}

		InternalAuth internalAuth = internalAuthDAO.findByUserAndPassword(user, password);
		if (internalAuth == null)
			throw new InvalidCredentialsException();

		if (!internalAuth.getVerified())
			throw new UserNotConfirmedException();
		
		String token = UUID.randomUUID().toString();
		String identifier = "INTERNAL-" + user.getId();
		
    return loginUser(AuthSource.INTERNAL, username, token, null, null, identifier, Arrays.asList(username), null, null, null, null, null);
  }
	
}

package fi.foyt.fni.auth;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
public class GuestAuthenticationStrategy extends AbstractInternalAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;

	@Override
	public boolean getSupportLogin() {
	  return true;
	}
	
	@Override
	public UserToken accessToken(HttpSession session, Locale locale, Map<String, String[]> parameters) throws MultipleEmailAccountsException,
			EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, InvalidCredentialsException, UserNotConfirmedException {
		return handleLogin(locale, systemSettingsController.getSetting("auth.guestUser.username"), systemSettingsController.getSetting("auth.guestUser.password"));
  }

}

package fi.foyt.fni.auth;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.system.SystemSettingsController;

@RequestScoped
public class GuestAuthenticationStrategy extends AbstractInternalAuthenticationStrategy {

	@Inject
	private SystemSettingsController systemSettingsController;

  @Override
  public AuthSource getAuthSource() {
    return AuthSource.GUEST;
  }

	@Override
	public boolean getSupportLogin() {
	  return true;
	}
	
	@Override
	public UserToken accessToken(Locale locale, Map<String, String[]> parameters) throws MultipleEmailAccountsException,
			EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, InvalidCredentialsException, UserNotConfirmedException {
		return handleLogin(locale, systemSettingsController.getSetting(SystemSettingKey.GUEST_USERNAME), systemSettingsController.getSetting(SystemSettingKey.GUEST_PASSWORD));
  }

}

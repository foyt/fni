package fi.foyt.fni.auth;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.auth.AuthSource;

@RequestScoped
public class AuthenticationStrategyManager {
	
	@Inject
	private Logger logger;

  @Inject
  private GoogleAuthenticationStrategy googleAuthenticationStrategy;

  @Inject
  private YahooAuthenticationStrategy yahooAuthenticationStrategy;

  @Inject
  private FacebookAuthenticationStrategy facebookAuthenticationStrategy;

  @Inject
  private DropboxAuthenticationStrategy dropboxAuthenticationStrategy;

  @Inject
  private UbuntuOneAuthenticationStrategy ubuntuOneAuthenticationStrategy;
  
  @Inject
  private InternalAuthenticationStrategy internalAuthenticationStrategy;

  @Inject
  private GuestAuthenticationStrategy guestAuthenticationStrategy;

  public AuthenticationStrategy getStrategy(AuthSource authSource) {
  	logger.info("trying to find strategy for source " + authSource);
  	
    switch (authSource) {
      case GOOGLE:
        return googleAuthenticationStrategy;
      case YAHOO:
        return yahooAuthenticationStrategy;
      case FACEBOOK:
        return facebookAuthenticationStrategy;
      case DROPBOX:
        return dropboxAuthenticationStrategy;
      case UBUNTU_ONE:
        return ubuntuOneAuthenticationStrategy;
      case INTERNAL:
        return internalAuthenticationStrategy;
      case GUEST:
        return guestAuthenticationStrategy;
    }

    return null;
  }
}
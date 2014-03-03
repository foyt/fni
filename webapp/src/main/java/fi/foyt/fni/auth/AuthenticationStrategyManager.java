package fi.foyt.fni.auth;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.auth.AuthSource;

@Dependent
public class AuthenticationStrategyManager implements Serializable {
	
  private static final long serialVersionUID = 3502263654436028790L;

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
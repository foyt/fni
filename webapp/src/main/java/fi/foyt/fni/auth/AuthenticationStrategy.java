package fi.foyt.fni.auth;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserEmailDAO;
import fi.foyt.fni.persistence.dao.users.UserTokenDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.UserIdentifier;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;

@RequestScoped
public abstract class AuthenticationStrategy {
	
	@Inject
	private UserController userController;

	@Inject
	private SessionController sessionController;

	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private UserIdentifierDAO userIdentifierDAO;
	
	@Inject
	private UserTokenDAO userTokenDAO;
	
	@Inject
	private UserEmailDAO userEmailDAO;
	
	@Inject
	private UserDAO userDAO;

	/**
	 * Returns whether authentication source can be used for logging in.
	 * 
	 * @return whether authentication source can be used for logging in.
	 */
	public abstract boolean getSupportLogin();
	
  public abstract UserToken accessToken(Locale locale, Map<String, String[]> parameters) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException, ExternalLoginFailedException, InvalidCredentialsException, UserNotConfirmedException ;
  
  protected synchronized UserToken loginUser(AuthSource authSource, String sourceId, String token, String tokenSecret, Date tokenExpires, String identifier, List<String> emails, String firstName, String lastName, String nickname, Locale locale, String[] grantedScopes) throws MultipleEmailAccountsException, EmailDoesNotMatchLoggedUserException, IdentityBelongsToAnotherUserException {
    if (locale == null) {
      locale = systemSettingsController.getDefaultLocale();
    }
    
    User loggedUser = sessionController.getLoggedUser();

    // TODO: Suggest user merge when multiple users are found with given emails
    
    boolean newlyCreated = false;
    User user = null;
    UserIdentifier userIdentifier = userIdentifierDAO.findByAuthSourceAndIdentifier(authSource, identifier);
    if (userIdentifier == null) {
    	// User has not logged in with this identity before.
    	
    	// We try to find a existing user by given email addresses
    	List<User> emailUsers = emails != null ? userEmailDAO.listUsersByEmails(emails) : null;
      
      if (emailUsers != null && emailUsers.size() > 1) {
      	// If we found more than one user with given e-mails, its a conflict.
      	throw new MultipleEmailAccountsException();
      }
      
      if (user == null && (emailUsers == null || emailUsers.size() == 0)) {
        if (loggedUser != null) {
          user = loggedUser;
        } else {
        	// If user is not logged in and no existing users by email were found 
        	// we need to create new user
          user = userController.createUser(firstName, lastName, null, locale, new Date(), UserProfileImageSource.GRAVATAR);
          newlyCreated = true;
        }
      } else {
      	if (emailUsers.size() == 1) {
    			// Existing account with given e-mail was found, so we attach this new identity to that account.
    			user = emailUsers.get(0);
      		
      		if (loggedUser != null) {
      			// If user was already logged in and we found user by email which does not match a logged user, its a conflict.
      			if (!loggedUser.getId().equals(user.getId())) {
      				throw new EmailDoesNotMatchLoggedUserException();
      			}
      		}
      	} else {
      		// If user is already logged in with another identity we try to attach 
      		// this new identity to that same user
      		user = loggedUser;
      	}
      }
      
      userIdentifier = userIdentifierDAO.create(user, authSource, sourceId, identifier);
    } else {
      user = userIdentifier.getUser();
      if ((loggedUser != null) && (!loggedUser.getId().equals(user.getId()))) {
      	throw new IdentityBelongsToAnotherUserException();
      }
    }
    
    if (!newlyCreated) {
      if (StringUtils.isBlank(user.getFirstName()) && StringUtils.isNotBlank(firstName)) {
        userDAO.updateFirstName(user, firstName);
      } 
      
      if (StringUtils.isBlank(user.getLastName()) && StringUtils.isNotBlank(lastName)) {
        userDAO.updateLastName(user, lastName);
      } 
      
      if (StringUtils.isBlank(user.getNickname()) && StringUtils.isNotBlank(nickname)) {
        userDAO.updateNickname(user, nickname);
      }
    }
    
    boolean primaryEmail = userEmailDAO.countByUser(user) == 0;
    if (emails != null) {
      for (String email : emails) {
        if (userEmailDAO.findByEmail(email) == null) {
          userEmailDAO.create(user, email, primaryEmail);
          primaryEmail = false;
        }
      }
    }
    
    return userTokenDAO.create(userIdentifier, token, tokenSecret, tokenExpires, StringUtils.join(grantedScopes, ","));
  }

	protected String getParameter(Map<String, String[]> parameters, String name) {
		String[] values = parameters.get(name);
		if (values != null && values.length == 1) 
			return values[0];
		
		return null;
	}
}

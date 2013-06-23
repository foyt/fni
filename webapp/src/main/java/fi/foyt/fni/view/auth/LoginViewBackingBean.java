package fi.foyt.fni.view.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.auth.AbstractInternalAuthenticationStrategy;
import fi.foyt.fni.auth.AuthenticationStrategy;
import fi.foyt.fni.auth.AuthenticationStrategyManager;
import fi.foyt.fni.auth.ConfigurationErrorException;
import fi.foyt.fni.auth.EmailDoesNotMatchLoggedUserException;
import fi.foyt.fni.auth.ExternalLoginFailedException;
import fi.foyt.fni.auth.IdentityBelongsToAnotherUserException;
import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.auth.InternalAuthenticationStrategy;
import fi.foyt.fni.auth.InvalidCredentialsException;
import fi.foyt.fni.auth.MultipleEmailAccountsException;
import fi.foyt.fni.auth.OAuthAuthenticationStrategy;
import fi.foyt.fni.auth.UserNotConfirmedException;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserConfirmKey;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.utils.view.ViewUtils;
import fi.foyt.fni.view.AbstractViewBackingBean;
import fi.foyt.fni.view.Locales;
import fi.foyt.fni.view.ViewControllerException;

@RequestScoped
@Named
@Stateful
public class LoginViewBackingBean extends AbstractViewBackingBean {
	
	@Inject
	private Logger logger;
	
	@Inject
  private SessionController sessionController;
  
  @Inject
  private AuthenticationStrategyManager authenticationStrategyManager;
  
	@Inject
	private AuthenticationController authenticationController;

	public void preRenderViewListener(ComponentSystemEvent event){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		
		String action = externalContext.getRequestParameterMap().get("a");
		String loginMethod = externalContext.getRequestParameterMap().get("loginMethod");
		
    String redirectUrl = externalContext.getRequestParameterMap().get("redirectUrl");
    if (StringUtils.isNotBlank(redirectUrl))
      AuthUtils.getInstance().storeRedirectUrl((HttpSession) externalContext.getSession(true), redirectUrl);

		if (StringUtils.isNotBlank(action)) {
		  handleActions(facesContext, externalContext, action);
  	} else if (StringUtils.isNotBlank(loginMethod)) {
  		handleExternalLogin(facesContext, externalContext, loginMethod);
  	}
	}	
	
	public String formLogin() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

    Map<String, String[]> parameters = new HashMap<String, String[]>();
    HttpSession session = (HttpSession) externalContext.getSession(true);
    Locale locale = sessionController.getLocale();
  	ResourceBundle locales = getLocales(facesContext);	
		
    AuthenticationStrategy authenticationStrategy = authenticationStrategyManager.getStrategy(AuthSource.INTERNAL);
    if (authenticationStrategy != null) {
    	try {
    		if (authenticationStrategy instanceof InternalAuthenticationStrategy) {
    			String username = externalContext.getRequestParameterMap().get("login:username");
    			String password = externalContext.getRequestParameterMap().get("login:passwordEncrypted");
    			
          parameters.put("username", new String[] { username });
          parameters.put("password", new String[] { password });
    			
          UserToken userToken = authenticationStrategy.accessToken(session, locale, parameters);
          if (userToken != null) {
            sessionController.login(userToken);
            String redirectUrl = AuthUtils.getInstance().retrieveRedirectUrl(session);
            if (StringUtils.isBlank(redirectUrl))
              redirectUrl = getBasePath(externalContext);
            
            externalContext.redirect(redirectUrl);
          } else {
          	addNotification(NotificationSeverity.WARNING, getLocalizedValue(locales, "auth.login.invalidCredentials"));
          }
    		}
    		
    	} catch (UserNotConfirmedException e) {
    		addNotification(NotificationSeverity.WARNING, getLocalizedValue(locales, "auth.login.userNotConfirmed"));
    	} catch (MultipleEmailAccountsException e) {
    		addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.userConflictMultipleEmailAccounts"));
    	} catch (EmailDoesNotMatchLoggedUserException e) {
    		addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.userConflictEmailDoesNotMatchLoggedUser"));
    	} catch (IdentityBelongsToAnotherUserException e) {
    		addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.userConflictIdentityBelongsToAnotherUser"));
			} catch (ExternalLoginFailedException e) {
				logger.log(Level.SEVERE, "Login with external authentication source failed", e);
				addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.externalLoginFailed"));
			} catch (InvalidCredentialsException e) {
      	addNotification(NotificationSeverity.WARNING, getLocalizedValue(locales, "auth.login.invalidCredentials"));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Login redirect failed because of malformed url", e);
				addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "generic.configurationError"));
			}
    } else {
    	addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.invalidAuthenticationStrategy"));
    }
    
    return null;
	}
	
	private void handleActions(FacesContext facesContext, ExternalContext externalContext, String actionName) {
	  String actionParams = externalContext.getRequestParameterMap().get("ap");
    Map<String, String> parameters = ViewUtils.explodeActionParameters(actionParams);
  	Action action = Action.valueOf(actionName);
  	ResourceBundle locales = getLocales(facesContext);	
  	
  	switch (action) {
  	  case VERIFY_EMAIL:
  		  handleVerifyEmailAction(locales, parameters);
  		break;
  	  case RESET_PASSWORD:
  		  handleResetPasswordAction(locales, parameters);
  	  break;
		}
  }
	
	private void handleVerifyEmailAction(ResourceBundle locales, Map<String, String> parameters) {
		String key = parameters.get("key");
		
		UserConfirmKey confirmKey = authenticationController.findConfirmKeyByKey(key);
		if (confirmKey == null) {
			addNotification(NotificationSeverity.SERIOUS, getLocalizedValue(locales, "auth.login.invalidVerificationKey"));
		} else {
  		User user = confirmKey.getUser();
  		InternalAuth internalAuth = authenticationController.findInternalAuthByUser(user);
  		if (internalAuth == null) {
  			addNotification(NotificationSeverity.SERIOUS, getLocalizedValue(locales, "auth.login.couldNotFindInternalAuth"));
  		} else {
    		authenticationController.verifyInternalAuth(confirmKey, internalAuth);
  			addNotification(NotificationSeverity.INFO, getLocalizedValue(locales, "auth.login.verificationSucceeded"));
  		}
		}
	}
	
	private void handleResetPasswordAction(ResourceBundle locales, Map<String, String> parameters) {
		String key = parameters.get("key");
		
		if (authenticationController.findPasswordResetKey(key) != null) {
			Map<String, String> jsParameters = new HashMap<>();
			jsParameters.put("key", key);
			addJavaScriptAction(Action.RESET_PASSWORD.toString(), jsParameters);
		} else {
			addNotification(NotificationSeverity.SERIOUS, getLocalizedValue(locales, "auth.login.invalidPasswordResetKey"));
		}
	}
	
	private void handleExternalLogin(FacesContext facesContext, ExternalContext externalContext, String loginMethod) {
    Map<String, String[]> parameters = externalContext.getRequestParameterValuesMap();
    HttpSession session = (HttpSession) externalContext.getSession(true);
    Locale locale = sessionController.getLocale();
  	ResourceBundle locales = getLocales(facesContext);	
    
    AuthenticationStrategy authenticationStrategy = authenticationStrategyManager.getStrategy(AuthSource.valueOf(loginMethod));
    if (authenticationStrategy != null) {
    	try {
        if (authenticationStrategy instanceof OAuthAuthenticationStrategy) {
          OAuthAuthenticationStrategy oAuthStrategy = (OAuthAuthenticationStrategy) authenticationStrategy;
          if (!authenticationStrategy.getSupportLogin() && !sessionController.isLoggedIn()) {
          	throw new ViewControllerException(Locales.getText(locale, "auth.error.authenticationStrategyDoesNotSupportLogginIn"));
          }
          
          // If return=1 we are returning from external authentication source 
          if ("1".equals(getRequestParameter(externalContext, "return"))) {
            UserToken userToken = oAuthStrategy.accessToken(session, locale, parameters);
            if (userToken != null) {
              sessionController.login(userToken);
              String redirectUrl = AuthUtils.getInstance().retrieveRedirectUrl(session);
              if (StringUtils.isBlank(redirectUrl))
                redirectUrl = getBasePath(externalContext);
              
              externalContext.redirect(redirectUrl);
            } else {
            	addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.externalLoginFailed"));
            }
          } else {
            String[] extraScopes = getRequestParameters(externalContext, "extraScopes");
            String redirectUrl = oAuthStrategy.authorize(session, extraScopes);
            externalContext.redirect(redirectUrl);
          }
        } else {
          AbstractInternalAuthenticationStrategy internalAuthenticationStrategy = (AbstractInternalAuthenticationStrategy) authenticationStrategy;
  
          UserToken userToken = internalAuthenticationStrategy.accessToken(session, locale, parameters);
          if (userToken != null) {
            sessionController.login(userToken);
            String redirectUrl = AuthUtils.getInstance().retrieveRedirectUrl(session);
            if (StringUtils.isBlank(redirectUrl))
              redirectUrl = getBasePath(externalContext);
            
            externalContext.redirect(redirectUrl);
          } else {
          	addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.externalLoginFailed"));
          }
        }
    	} catch (InvalidCredentialsException e) {
    		addNotification(NotificationSeverity.WARNING, getLocalizedValue(locales, "auth.login.invalidCredentials"));
    	} catch (UserNotConfirmedException e) {
    		addNotification(NotificationSeverity.WARNING, getLocalizedValue(locales, "auth.login.userNotConfirmed"));
    	} catch (MultipleEmailAccountsException e) {
    		addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.userConflictMultipleEmailAccounts"));
    	} catch (EmailDoesNotMatchLoggedUserException e) {
    		addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.userConflictEmailDoesNotMatchLoggedUser"));
    	} catch (IdentityBelongsToAnotherUserException e) {
    		addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.userConflictIdentityBelongsToAnotherUser"));
    	} catch (ConfigurationErrorException e) {
				logger.log(Level.SEVERE, "Login failed because of configuration error", e);
				addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "generic.configurationError"));
			} catch (ExternalLoginFailedException e) {
				logger.log(Level.SEVERE, "Login with external authentication source failed", e);
				addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.externalLoginFailed"));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Login redirect failed because of malformed url", e);
				addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "generic.configurationError"));
			}
    } else {
    	addNotification(NotificationSeverity.CRITICAL, getLocalizedValue(locales, "auth.login.invalidAuthenticationStrategy"));
    }
  }
	
	private enum Action {
		VERIFY_EMAIL,
		RESET_PASSWORD
	}
}
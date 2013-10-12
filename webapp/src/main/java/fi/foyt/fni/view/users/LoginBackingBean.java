package fi.foyt.fni.view.users;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.auth.AuthenticationStrategy;
import fi.foyt.fni.auth.AuthenticationStrategyManager;
import fi.foyt.fni.auth.ConfigurationErrorException;
import fi.foyt.fni.auth.EmailDoesNotMatchLoggedUserException;
import fi.foyt.fni.auth.ExternalLoginFailedException;
import fi.foyt.fni.auth.IdentityBelongsToAnotherUserException;
import fi.foyt.fni.auth.InternalAuthenticationStrategy;
import fi.foyt.fni.auth.InvalidCredentialsException;
import fi.foyt.fni.auth.MultipleEmailAccountsException;
import fi.foyt.fni.auth.OAuthAuthenticationStrategy;
import fi.foyt.fni.auth.UserNotConfirmedException;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.PasswordResetKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.persistence.model.users.UserToken;
import fi.foyt.fni.persistence.model.users.UserVerificationKey;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.utils.faces.FacesUtils;
import fi.foyt.fni.utils.mail.MailUtils;

@RequestScoped
@Named
@Stateful
@URLMappings(mappings = {
  @URLMapping(
		id = "users-login", 
		pattern = "/login/", 
		viewId = "/users/login.jsf"
  )
})
public class LoginBackingBean {

	@Inject
	private Logger logger;

	@Inject
	private UserController userController;

	@Inject
	private SessionController sessionController;

	@Inject
	private AuthenticationStrategyManager authenticationStrategyManager;

	@Inject
	private AuthenticationController authenticationController;

	@Inject
	private SystemSettingsController systemSettingsController;
	
	public void preRenderViewListener(ComponentSystemEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();

		String loginMethod = externalContext.getRequestParameterMap().get("loginMethod");

		String redirectUrl = externalContext.getRequestParameterMap().get("redirectUrl");
		if (StringUtils.isNotBlank(redirectUrl))
			AuthUtils.getInstance().storeRedirectUrl((HttpSession) externalContext.getSession(true), redirectUrl);

		if (StringUtils.isNotBlank(loginMethod)) {
			AuthSource authSource = AuthSource.valueOf(loginMethod);
			if (authSource != null) {
			  handleExternalLogin(facesContext, externalContext, authSource);
			}
		}
	}

	public String getLoginEmail() {
		return loginEmail;
	}

	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	public void login() {
		AuthenticationStrategy authenticationStrategy = authenticationStrategyManager.getStrategy(AuthSource.INTERNAL);
		if (authenticationStrategy != null) {
			try {
				if (authenticationStrategy instanceof InternalAuthenticationStrategy) {
					FacesContext facesContext = FacesContext.getCurrentInstance();
					ExternalContext externalContext = facesContext.getExternalContext();

					HttpSession session = (HttpSession) externalContext.getSession(true);
					Locale locale = sessionController.getLocale();

					Map<String, String[]> parameters = new HashMap<String, String[]>();
					parameters.put("username", new String[] { getLoginEmail() });
					parameters.put("password", new String[] { getLoginPassword() });

					UserToken userToken = authenticationStrategy.accessToken(session, locale, parameters);
					if (userToken != null) {
						sessionController.login(userToken);
						String redirectUrl = AuthUtils.getInstance().retrieveRedirectUrl(session);
						if (StringUtils.isBlank(redirectUrl)) {
							externalContext.redirect(new StringBuilder().append(externalContext.getRequestContextPath()).append("/").toString());
						} else {
							externalContext.redirect(redirectUrl);
						}
					} else {
						FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.invalidCredentials"));
					}
				}

			} catch (UserNotConfirmedException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.userNotVerified"));
			} catch (MultipleEmailAccountsException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.userConflictMultipleEmailAccounts"));
			} catch (EmailDoesNotMatchLoggedUserException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.userConflictEmailDoesNotMatchLoggedUser"));
			} catch (IdentityBelongsToAnotherUserException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.userConflictIdentityBelongsToAnotherUser"));
			} catch (ExternalLoginFailedException e) {
				logger.log(Level.SEVERE, "Login with external authentication source failed", e);
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.externalLoginFailed"));
			} catch (InvalidCredentialsException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.invalidCredentials"));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Login redirect failed because of malformed url", e);
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, "Internal Error");
			}
		} else {
			logger.severe("Could not find internal authentication strategy");
		}
	}

	public String getRegisterFirstName() {
		return registerFirstName;
	}

	public void setRegisterFirstName(String registerFirstName) {
		this.registerFirstName = registerFirstName;
	}

	public String getRegisterLastName() {
		return registerLastName;
	}

	public void setRegisterLastName(String registerLastName) {
		this.registerLastName = registerLastName;
	}

	public String getRegisterEmail() {
		return registerEmail;
	}

	public void setRegisterEmail(String registerEmail) {
		this.registerEmail = registerEmail;
	}

	public String getRegisterPassword1() {
		return registerPassword1;
	}

	public void setRegisterPassword1(String registerPassword1) {
		this.registerPassword1 = registerPassword1;
	}

	public String getRegisterPassword2() {
		return registerPassword2;
	}

	public void setRegisterPassword2(String registerPassword2) {
		this.registerPassword2 = registerPassword2;
	}

	public void register() {
		boolean valid = true;
		
		if (StringUtils.isBlank(getRegisterPassword1())) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.registerPasswordRequired"));
			valid = false;
		}
		
		if (valid && !getRegisterPassword1().equals(getRegisterPassword2())) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.registrationPasswordsDontMatch"));
			valid = false;
		}

		if (valid) {
		  if (DigestUtils.md5Hex("").equals(getRegisterPassword1())) {
			  FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.registerPasswordRequired"));
			  valid = false;
	  	}
		}

		if (valid) {
			User existingUser = userController.findUserByEmail(getRegisterEmail());
			if (existingUser != null) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.registrationUserWithSpecifiedEmailAlreadyExists"));
			} else {
				Locale locale = sessionController.getLocale();
				User user = userController.createUser(getRegisterFirstName(), getRegisterLastName(), null, locale, new Date(), UserProfileImageSource.GRAVATAR);
				userController.createUserEmail(user, getRegisterEmail(), Boolean.TRUE);
				
				UserVerificationKey verificationKey = authenticationController.createVerificationKey(user, getRegisterEmail());
				authenticationController.createInternalAuth(user, getRegisterPassword1());

				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				String verifyUrl = new StringBuilder().append(externalContext.getRequestScheme()).append("://").append(externalContext.getRequestServerName())
						.append(":").append(externalContext.getRequestServerPort()).append(externalContext.getRequestContextPath())
						.append("/users/verify/" + verificationKey.getValue()).toString();

				String mailTitle = FacesUtils.getLocalizedValue("users.login.verificationEmailTitle");
				String mailContent = FacesUtils.getLocalizedValue("users.login.verificationEmailContent", verifyUrl);

				try {
					String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
					String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
					MailUtils.sendMail(fromMail, fromName, getRegisterEmail(), user.getFullName(), mailTitle, mailContent, "text/plain");
					FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.login.verificationEmailSent"));
				} catch (MessagingException e) {
					FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.verificationSendingFailed"));
				}

			}
		}
	}

	public String getForgotPasswordEmail() {
		return forgotPasswordEmail;
	}

	public void setForgotPasswordEmail(String forgotPasswordEmail) {
		this.forgotPasswordEmail = forgotPasswordEmail;
	}

	public void forgotPassword() {
		if (StringUtils.isBlank(getForgotPasswordEmail())) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.resetPasswordEmail"));
		} else {
			User user = userController.findUserByEmail(getForgotPasswordEmail());
			if (user != null) {
				PasswordResetKey resetKey = authenticationController.generatePasswordResetKey(user);

				ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				String verifyUrl = new StringBuilder().append(externalContext.getRequestScheme()).append("://").append(externalContext.getRequestServerName())
						.append(":").append(externalContext.getRequestServerPort()).append(externalContext.getRequestContextPath())
						.append("/users/resetpassword/" + resetKey.getValue()).toString();

				String mailTitle = FacesUtils.getLocalizedValue("users.login.resetPasswordEmailTitle");
				String mailContent = FacesUtils.getLocalizedValue("users.login.resetPasswordEmailContent", verifyUrl);

				try {
					String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
					String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
					MailUtils.sendMail(fromMail, fromName, getForgotPasswordEmail(), user.getFullName(), mailTitle, mailContent, "text/html");
					FacesUtils.addMessage(FacesMessage.SEVERITY_INFO, FacesUtils.getLocalizedValue("users.login.resetPasswordEmailSent", getForgotPasswordEmail()));
				} catch (MessagingException e) {
					FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.resetPasswordSendingFailed"));
				}

			} else {
				FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("users.login.resetPasswordUserNotFound"));
			}
		}
	}

	private void handleExternalLogin(FacesContext facesContext, ExternalContext externalContext, AuthSource authSource) {
		Map<String, String[]> parameters = externalContext.getRequestParameterValuesMap();
		HttpSession session = (HttpSession) externalContext.getSession(true);

		AuthenticationStrategy authenticationStrategy = authenticationStrategyManager.getStrategy(authSource);
		if (authenticationStrategy != null) {
			try {
				if (authenticationStrategy instanceof OAuthAuthenticationStrategy) {
					OAuthAuthenticationStrategy oAuthStrategy = (OAuthAuthenticationStrategy) authenticationStrategy;
					if (!authenticationStrategy.getSupportLogin() && !sessionController.isLoggedIn()) {
						FacesUtils.addMessage(FacesMessage.SEVERITY_ERROR, FacesUtils.getLocalizedValue("users.login.authenticationStrategyDoesNotSupportLogginIn"));
					} else {
						// If return=1 we are returning from external authentication source
						if ("1".equals(externalContext.getRequestParameterMap().get("return"))) {
							Locale locale = externalContext.getRequestLocale();
							
							UserToken userToken = oAuthStrategy.accessToken(session, locale, parameters);
							if (userToken != null) {
								sessionController.login(userToken);
								String redirectUrl = AuthUtils.getInstance().retrieveRedirectUrl(session);
								if (StringUtils.isBlank(redirectUrl)) {
									redirectUrl = new StringBuilder()
									  .append(externalContext.getRequestContextPath())
									  .append("/").toString();
								}
								
								externalContext.redirect(redirectUrl);
							} else {
								FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.externalLoginFailed"));
							}
						} else {
							String[] extraScopes = parameters.get("extraScopes");
							String redirectUrl = oAuthStrategy.authorize(session, extraScopes);
							externalContext.redirect(redirectUrl);
						}
					}
				}
			} catch (MultipleEmailAccountsException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.userConflictMultipleEmailAccounts"));
			} catch (EmailDoesNotMatchLoggedUserException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.userConflictEmailDoesNotMatchLoggedUser"));
			} catch (IdentityBelongsToAnotherUserException e) {
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.userConflictIdentityBelongsToAnotherUser"));
			} catch (ConfigurationErrorException e) {
				logger.log(Level.SEVERE, "Login failed because of configuration error", e);
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("generic.configurationError"));
			} catch (ExternalLoginFailedException e) {
				logger.log(Level.SEVERE, "Login with external authentication source failed", e);
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.externalLoginFailed"));
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Login redirect failed because of malformed url", e);
				FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("generic.configurationError"));
			}
		} else {
			FacesUtils.addMessage(FacesMessage.SEVERITY_FATAL, FacesUtils.getLocalizedValue("users.login.invalidAuthenticationStrategy"));
		}
	}

	private String loginEmail;
	private String loginPassword;
	private String registerFirstName;
	private String registerLastName;
	private String registerEmail;
	private String registerPassword1;
	private String registerPassword2;
	private String forgotPasswordEmail;

}

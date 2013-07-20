package fi.foyt.fni.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.api.beans.CompactMaterialBean;
import fi.foyt.fni.api.beans.CompactUserBean;
import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.persistence.dao.auth.InternalAuthDAO;
import fi.foyt.fni.persistence.dao.auth.UserIdentifierDAO;
import fi.foyt.fni.persistence.dao.materials.ImageDAO;
import fi.foyt.fni.persistence.dao.users.UserConfirmKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserEmailDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingDAO;
import fi.foyt.fni.persistence.dao.users.UserSettingKeyDAO;
import fi.foyt.fni.persistence.model.auth.AuthSource;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageSize;
import fi.foyt.fni.persistence.model.materials.MaterialThumbnail;
import fi.foyt.fni.persistence.model.users.PasswordResetKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserConfirmKey;
import fi.foyt.fni.persistence.model.users.UserEmail;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.UserSetting;
import fi.foyt.fni.persistence.model.users.UserSettingKey;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.mail.MailUtils;

@Path("/users")
@RequestScoped
@Stateful
@Produces ("application/json")
public class UsersRESTService extends RESTService {
	
	private static final String CONFIRM_LINK = "%s/login/?a=VERIFY_EMAIL&ap=key:%s";
	private static final String RESET_PASSWORD_LINK = "%s/login/?a=RESET_PASSWORD&ap=key:%s";
	
	@Inject
	private Logger logger;

	@Inject
	private UserController userController;

	@Inject
	private SessionController sessionController;
	
	@Inject
	private SystemSettingsController systemSettingsController;

	@Inject
	private MaterialController materialController;
	
	@Inject
	private AuthenticationController authenticationController;
	
	@Inject
	private UserEmailDAO userEmailDAO;

	@Inject
	private UserConfirmKeyDAO userConfirmKeyDAO;

	@Inject
	private InternalAuthDAO internalAuthDAO;

	@Inject
	private UserIdentifierDAO userIdentifierDAO;
	
	@Inject
	private UserDAO userDAO;
	
	@Inject
	private ImageDAO imageDAO;

	@Inject
	private UserSettingKeyDAO userSettingKeyDAO;

	@Inject
	private UserSettingDAO userSettingDAO;
	
	/**
   * Creates new user
	 * @return 
   */
	@POST
	@Path ("/createUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createUser(
			@FormParam("firstName") String firstName, 
			@FormParam("lastName") String lastName, 
			@FormParam("email") String email, 
			@FormParam("password") String password, 
			@FormParam("redirectUrl") String redirectUrl,
			@FormParam("locale") String localeParam,
			@Context HttpHeaders httpHeaders,
			@Context UriInfo uriInfo) {
		
	  Date registrationDate = new Date();
		Locale browserLocale = getBrowserLocale(httpHeaders);
		
		if (StringUtils.isBlank(firstName)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "firstName")).build();
		}
		
		if (StringUtils.isBlank(lastName)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "lastName")).build();
		}
		
		if (StringUtils.isBlank(email)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "email")).build();
		}
		
		if (StringUtils.isBlank(password)) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "password")).build();
		}
		
		Locale locale = StringUtils.isBlank(localeParam) ? null : new Locale(localeParam);
		if (locale == null)
			locale = browserLocale;
		
		UserEmail userEmail = userEmailDAO.findByEmail(email);
		if (userEmail != null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.users.createUser.userWithSpecifiedEmailAlreadyExists")).build();
		}
		
		User user = userController.createUser(firstName, lastName, null, locale, registrationDate);
		userEmailDAO.create(user, email, Boolean.TRUE);
		internalAuthDAO.create(user, password, Boolean.FALSE);
		userIdentifierDAO.create(user, AuthSource.INTERNAL, email, "INTERNAL-" + user.getId());
		
		try {
		  sendConfirmEmail(user, email, "users.createUser.confirmEmailTitle", "users.createUser.confirmEmailContent", browserLocale, uriInfo, redirectUrl);
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, "UTF-8 not supported", e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send an e-mail", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.users.createUser.couldNotSendEmail")).build();
    } catch (MalformedURLException e) {
      logger.log(Level.SEVERE, "Malformed confirm url", e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
		}

		return Response.ok(new ApiResult<>(CompactUserBean.fromEntity(user))).build();
	}
	
	@PUT
	@POST
	@Path ("/{USERID}/updateBasicInfo")
	public Response updateBasicInfo(
			@PathParam ("USERID") String userId,
			@FormParam ("firstName") String firstName,
			@FormParam ("lastName") String lastName,
			@FormParam ("nickname") String nickname,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		User user = null;
		
		if ("SELF".equals(userId)) {
			user = loggedUser;
		} else {
  		if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
		}
		
		userDAO.updateFirstName(user, firstName);
		userDAO.updateLastName(user, lastName);
		userDAO.updateNickname(user, nickname);
		
		return Response.ok(new ApiResult<>(CompactUserBean.fromEntity(user))).build();
	}
	
	@PUT
	@POST
	@Path ("/{USERID}/updateProfileImage")
	public Response updateProfileImage(
			@PathParam ("USERID") String userIdParam,
			@FormParam ("imageId") Long imageId,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
    
		User user = null;
		User loggedUser = getLoggedUser(httpHeaders);
		
		if ("SELF".equals(userIdParam)) {
  		if (!hasRole(loggedUser, UserRole.GUEST)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
  		user = loggedUser;
		} else {
			if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
			
			Long userId =  NumberUtils.createLong(userIdParam);
			user = userDAO.findById(userId);
		}

		Image image = imageDAO.findById(imageId);
		userDAO.updateProfileImage(user, image);
		
		return Response.ok(new ApiResult<>(CompactMaterialBean.fromMaterialEntity(image))).build();
	}
	
	@PUT
	@POST
	@Path ("/{USERID}/changeLocale")
	public Response changeLocale(
			@PathParam ("USERID") String userIdParam,
			@FormParam ("locale") String localeParam,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
    
		User user = null;
		Locale locale = LocaleUtils.toLocale(localeParam);
		User loggedUser = getLoggedUser(httpHeaders);

		if ("ANONYMOUS".equals(userIdParam)) {
			if (loggedUser == null) {
  		  sessionController.setLocale(locale);
			} else {
  		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		} else {
  		if ("SELF".equals(userIdParam)) {
    		user = loggedUser;
  		} else {
  		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
  		
  		userDAO.updateLocale(user, locale);
  		sessionController.setLocale(locale);
		}
		
		return Response.ok(new ApiResult<>(null)).build();
	}
	
	@POST
	@Path ("/{USERID}/createInternalAuth")
	public Response createInternalAuth(
    @PathParam ("USERID") String userIdParam, 
		@FormParam ("password") String password,
		@FormParam ("redirectUrl") String redirectUrl,
		@Context HttpHeaders httpHeaders,
		@Context UriInfo uriInfo) {
		
		boolean currentUser = "SELF".equals(userIdParam);
		Locale browserLocale = getBrowserLocale(httpHeaders);
    
		User user = null;
		User loggedUser = getLoggedUser(httpHeaders);

		if (currentUser) {
  		if (!hasRole(loggedUser, UserRole.USER)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
  		
  		user = loggedUser;
		} else {
  		if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
			
			Long userId =  NumberUtils.createLong(userIdParam);
			user = userDAO.findById(userId);
		}
		
		UserEmail userEmail = userEmailDAO.findByUserAndPrimary(user, Boolean.TRUE);
		if (userEmail == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.users.createInternalAuth.noPrimaryEmail")).build();
		}
		
		internalAuthDAO.create(user, password, Boolean.FALSE);
		userIdentifierDAO.create(user, AuthSource.INTERNAL, userEmail.getEmail(), "INTERNAL-" + user.getId());
		
		try {
      sendConfirmEmail(user, userEmail.getEmail(), "users.createInternalAuth.confirmEmailTitle", "users.createInternalAuth.confirmEmailContent", browserLocale, uriInfo, redirectUrl);
    } catch (UnsupportedEncodingException e) {
      logger.log(Level.SEVERE, "UTF-8 not supported", e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    } catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send an e-mail", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.users.createUser.couldNotSendEmail")).build();
    } catch (MalformedURLException e) {
      logger.log(Level.SEVERE, "Malformed confirm url", e);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("confirmEmail", userEmail.getEmail());
		
		return Response.ok(new ApiResult<>(result)).build();
	}
	
	@POST
	@PUT
	@Path ("/{USERID}/updateInternalAuth")
	public Response updateInternalAuth(
    @PathParam ("USERID") String userIdParam, 
		@FormParam ("oldPassword") String oldPassword,
		@FormParam ("newPassword") String newPassword,
		@Context HttpHeaders httpHeaders) {
		
		boolean currentUser = "SELF".equals(userIdParam);
		Locale browserLocale = getBrowserLocale(httpHeaders);
    
		User user = null;
		User loggedUser = getLoggedUser(httpHeaders);
		InternalAuth internalAuth = null;

		if (currentUser) {
  		if (!hasRole(loggedUser, UserRole.USER)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
  		
  		user = loggedUser;

  		internalAuth = internalAuthDAO.findByUserAndPassword(user, oldPassword);
		} else {
  		if (!hasRole(loggedUser, UserRole.ADMINISTRATOR)) {
  			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
			
			Long userId =  NumberUtils.createLong(userIdParam);
			user = userDAO.findById(userId);

  		internalAuth = internalAuthDAO.findByUser(user);
		}
		
		if (internalAuth != null) {
			internalAuthDAO.updatePassword(internalAuth, newPassword);
			return Response.ok(new ApiResult<Object>(null)).build();
		} else {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
	}
	
	@GET
	@Path ("/{USERID}/profileImage/{SIZE}")
	public Response profileImage(
			@PathParam ("USERID") String userIdParam, 
			@PathParam ("SIZE") String sizeParam,
			@Context HttpHeaders httpHeaders) {
		
		boolean currentUser = "SELF".equals(userIdParam);
		Locale browserLocale = getBrowserLocale(httpHeaders);
    
		User user = null;
		
		if (currentUser) {
  		User loggedUser = getLoggedUser(httpHeaders);
  		if (!hasRole(loggedUser, UserRole.GUEST)) {
  		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
  		user = loggedUser;
		} else {
			Long userId =  NumberUtils.createLong(userIdParam);
			user = userDAO.findById(userId);
		}
		
		ImageSize size = sizeParam == null || sizeParam.equals("ORIGINAL") ? ImageSize.ORIGINAL : ImageSize.valueOf('_' + sizeParam);
    
    if (user == null) {
      return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "error.users.profile.userNotFound")).build();
    }
    
    Image profileImage = user.getProfileImage();
    if (profileImage == null) {
    	return Response.status(Response.Status.NOT_FOUND).build();
    }
    
    try {
    	MaterialThumbnail imageThumbnail = materialController.getImageThumbnail(profileImage, size);
    	return createBinaryResponse(imageThumbnail.getContent(), imageThumbnail.getContentType());
    } catch (IOException e) {
    	return Response.noContent().build();
    }
	}
	
	@POST
	@Path ("/{USERID}/updateUserSettings")
	public Response updateUserSettings(
			@PathParam ("USERID") String userIdParam,
			@FormParam ("keys") String keysParam,
			@FormParam ("values") String valuesParam,
			@Context HttpHeaders httpHeaders) {
		
		boolean currentUser = "SELF".equals(userIdParam);
		Locale browserLocale = getBrowserLocale(httpHeaders);
    
		User user = null;
		
		if (currentUser) {
  		User loggedUser = getLoggedUser(httpHeaders);
  		if (!hasRole(loggedUser, UserRole.USER)) {
  		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
  		}
  		user = loggedUser;
		} else {
		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		if (StringUtils.isBlank(keysParam)) {
		  return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "keys")).build();
		}
		
		if (StringUtils.isBlank(valuesParam)) {
		  return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.missingParameter", "values")).build();
		}
		
		String[] keys = keysParam.split(",");
		String[] values = valuesParam.split(",");
		
		if (keys.length != values.length) {
		  return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "values")).build();
		}
		
		for (int i = 0, l = keys.length; i < l; i++) {
			String key = keys[i];
			String value = values[i];
			UserSettingKey userSettingKey = userSettingKeyDAO.findByKey(key);
			if (userSettingKey == null) {
			  return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "key")).build();
			}
			
			UserSetting userSetting = userSettingDAO.findByUserAndUserSettingKey(user, userSettingKey);
			if (userSetting != null) {
				userSettingDAO.updateValue(userSetting, value);
			} else {
				userSettingDAO.create(user, userSettingKey, value);
			}
		}
		
		return Response.ok(new ApiResult<Object>(null)).build();
	}
	
	@POST
	@Path ("/resetPassword")
	public Response resetPassword(
			@FormParam("email") String email,
			@Context HttpHeaders httpHeaders,
			@Context UriInfo uriInfo) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
    User user = userController.findUserByEmail(email);
		if (user == null) {
		  return Response.status(Response.Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "users.resetPassword.error.userNotFound")).build();
		}
		
		PasswordResetKey resetKey = authenticationController.generatePasswordResetKey(user);
		
		String resetLink;
		try {
			resetLink = String.format(RESET_PASSWORD_LINK, getApplicationBaseUrl(uriInfo), resetKey.getValue());
		} catch (MalformedURLException e1) {
      logger.log(Level.SEVERE, "Malformed password reset url", e1);
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
		}
		
		String fromName = systemSettingsController.getSetting("system.mailer.name");
		String fromMail = systemSettingsController.getSetting("system.mailer.mail");
		String title = ApiMessages.getText(browserLocale, "users.resetPassword.resetPasswordEmailTitle");
		String content = ApiMessages.getText(browserLocale, "users.resetPassword.resetPasswordEmailContent", resetLink);
		
		try {
      MailUtils.sendMail(fromMail, fromName, email, user.getFullName(), title, content, "text/html");
		} catch (MessagingException e) {
      logger.log(Level.SEVERE, "Could not send an e-mail", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "users.resetPassword.error.couldNotSendEmail")).build();
    }
    
		return Response.ok(
		  new ApiResult<Object>(null)		
		).build();
	}

	@GET
	@Path ("/resetPassword/{KEY}")
	public Response resetPasswordKey(
			@PathParam("KEY") String key,
			@QueryParam ("password") String password, 
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);

		PasswordResetKey passwordResetKey = authenticationController.findPasswordResetKey(key);
		if (passwordResetKey != null) {
			authenticationController.setUserPassword(passwordResetKey.getUser(), password);
			authenticationController.deletePasswordResetKey(passwordResetKey);
		} else {
      logger.log(Level.SEVERE, "Invalid password reset key");
      return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "users.resetPassword.error.invalidPasswordResetKey")).build();
		}

		return Response.ok(
		  new ApiResult<Object>(null)
	  ).build();
	}
	
	private void sendConfirmEmail(User user, String email, String titleLocale, String contentLocale, Locale browserLocale, UriInfo uriInfo, String redirectUrl) throws MessagingException, UnsupportedEncodingException, MalformedURLException {
	  UserConfirmKey confirmKey = userConfirmKeyDAO.create(user, UUID.randomUUID().toString());
	  
	  String confirmLink = String.format(CONFIRM_LINK, getApplicationBaseUrl(uriInfo), confirmKey.getValue());
	  if (StringUtils.isNotBlank(redirectUrl)) {
      confirmLink += "&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8");
		}
		
		String fromName = systemSettingsController.getSetting("system.mailer.name");
		String fromMail = systemSettingsController.getSetting("system.mailer.mail");
		String title = ApiMessages.getText(browserLocale, titleLocale);
		String content = ApiMessages.getText(browserLocale, contentLocale, confirmLink);
		
    MailUtils.sendMail(fromMail, fromName, email, user.getFullName(), title, content, "text/plain");
  }
}

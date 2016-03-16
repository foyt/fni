package fi.foyt.fni.rest.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryParser.ParseException;

import fi.foyt.fni.auth.AuthenticationController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.mail.Mailer;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserProfileImageSource;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.OAuthScopes;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.search.SearchResult;

/**
 * User REST services
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateful
@RequestScoped
public class UserRestServices {

  @Inject
  private Logger logger;
  
  @Inject
  private SessionController sessionController;

  @Inject
  private UserController userController;

  @Inject
  private SystemSettingsController systemSettingsController;

  @Inject
  private AuthenticationController authenticationController;

  @Inject
  private Mailer mailer;
  
  /**
   * Creates new user
   * 
   * @param entity payload
   * @param generateCredentials whether to generate credentials for new user (defaults to true)
   * @param sendCredentials whether to send generated credentials to user via email (defaults to true)
   * @return Response response
   * @responseType fi.foyt.fni.rest.users.model.User
   */
  @Path("/users")
  @POST
  @Security (
    allowService = true,
    scopes = { OAuthScopes.USERS_USER_CREATE }
  )
  public Response createUser(fi.foyt.fni.rest.users.model.User entity, @QueryParam ("generateCredentials") @DefaultValue ("TRUE") Boolean generateCredentials, @QueryParam ("sendCredentials") @DefaultValue ("TRUE") Boolean sendCredentials, @QueryParam ("password") String password) {
    if (entity.getEmails() == null || entity.getEmails().isEmpty()) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Email address is required").build();
    }
    
    if (!generateCredentials) {
      if (StringUtils.isBlank(password)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("password is required when generateCredentials is set to false").build();
      }
    }
    
    Locale locale = null;
    try {
      locale = LocaleUtils.toLocale(entity.getLocale());
      if (locale == null) {
        locale = systemSettingsController.getDefaultLocale();
      }
      
      if (!systemSettingsController.isSupportedLocale(locale)) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Unsupported locale").build();
      }
    } catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build(); 
    }
    
    ArrayList<String> emails = new ArrayList<>(entity.getEmails());
    for (String email : emails) {
      if (userController.findByEmail(email) != null) {
        return Response.status(Response.Status.BAD_REQUEST).entity("Email is already in use").build();
      }
    }
    
    User user = userController.createUser(entity.getFirstName(), entity.getLastName(), entity.getNickname(), locale, new Date(), UserProfileImageSource.GRAVATAR);
    String primaryMail = emails.remove(0);
    userController.createUserEmail(user, primaryMail, Boolean.TRUE);
    
    for (String email : emails) {
      userController.createUserEmail(user, email, Boolean.FALSE);
    }
    
    String unencryptedPassword = null;
    if (generateCredentials) {
      unencryptedPassword = RandomStringUtils.randomAlphanumeric(8);
    } else {
      unencryptedPassword = password;
    }
    
    InternalAuth internalAuth = authenticationController.createInternalAuth(user, DigestUtils.md5Hex(unencryptedPassword));
    // TODO: verification
    authenticationController.verifyInternalAuth(internalAuth);
    
    if (sendCredentials) {
      String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
      String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
      String subject = ExternalLocales.getText(locale, "rest.users.generatedPasswordEmailSubject");
      String content = ExternalLocales.getText(locale, "rest.users.generatedPasswordEmailContent", unencryptedPassword);
      try {
        mailer.sendMail(fromMail, fromName, userController.getUserPrimaryEmail(user), userController.getUserDisplayName(user), subject, content, "text/plain");
      } catch (MessagingException e) {
        logger.log(Level.SEVERE, "Could not deliver email", e);
      }
    }
    
    return Response.ok(createRestModel(user)).build();
  }
  
  /**
   * Lists users
   * 
   * @param email filter responses by email
   * @return Response response
   * @responseType java.util.List<fi.foyt.fni.rest.users.model.User>
   */
  @Path("/users")
  @GET
  @Security (
    allowService = true,
    allowNotLogged = false,
    scopes = { OAuthScopes.USERS_USER_LIST }
  )
  public Response listUsers(@QueryParam ("email") String email, @QueryParam ("search") String search, @QueryParam ("maxResults") @DefaultValue ("10") Integer maxResults) {
    List<User> result = new ArrayList<>();
    
    if (StringUtils.isNotBlank(email)) {
      User user = userController.findUserByEmail(email);
      if (user != null) {
        result.add(user);
      }
    } else if (StringUtils.isNotBlank(search)) {
      try {
        List<SearchResult<User>> searchResults = userController.searchUsers(search, maxResults);
        for (SearchResult<User> searchResult : searchResults) {
          result.add(searchResult.getEntity());
        }
      } catch (ParseException e) {
        logger.log(Level.SEVERE, "Failed to parse search string", e);
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
      }
    } else {
      return Response.status(Status.NOT_IMPLEMENTED).entity("Listing all users is not implemented").build();
    }
    
    return Response.ok(createRestModel(result.toArray(new User[0]))).build();
  }
  
  /**
   * Returns user info
   * 
   * @return Response response
   * @responseType fi.foyt.fni.rest.users.model.User
   */
  @Path("/users/{ID:[0-9]*}")
  @Security (
    allowNotLogged = false,
    allowService = false,
    scopes = OAuthScopes.USERS_USER_FIND
  )
  @GET
  public Response findUser(@PathParam ("ID") Long id) {
    User user = userController.findUserById(id);
    if (user == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (user.getArchived()) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    return Response.ok(createRestModel(user)).build();
  }
  
  @Path("/users/me")
  @Security (
    allowNotLogged = false,
    allowService = false,
    scopes = OAuthScopes.USERS_USER_FIND_ME
  )
  @GET
  public Response getOwnInfo() {
    User loggedUser = sessionController.getLoggedUser();
    return Response.ok(createRestModel(loggedUser)).build();
  }
  
  private fi.foyt.fni.rest.users.model.User createRestModel(fi.foyt.fni.persistence.model.users.User user) {
    List<String> emails = userController.getUserEmails(user);
    return new fi.foyt.fni.rest.users.model.User(user.getId(), user.getFirstName(), user.getLastName(), user.getNickname(), user.getLocale(), emails);
  }

  private fi.foyt.fni.rest.users.model.User[] createRestModel(fi.foyt.fni.persistence.model.users.User... users) {
    List<fi.foyt.fni.rest.users.model.User> result = new ArrayList<>();
    
    for (User user : users) {
      result.add(createRestModel(user)); 
    }
    
    return result.toArray(new fi.foyt.fni.rest.users.model.User[0]);
  }

}

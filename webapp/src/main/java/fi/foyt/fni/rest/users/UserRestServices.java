package fi.foyt.fni.rest.users;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

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
  private SessionController sessionController;

  @Inject
  private UserController userController;
  
  /**
   * Creates new user
   * 
   * @param user payload
   * @param generateCredentials whether to generate credentials for new user (defaults to true)
   * @param sendNotification whether to send notification to user (defaults to true)
   * @return Response response
   * @responseType fi.foyt.fni.rest.users.model.User
   */
  @Path("/users")
  @POST
  public Response createUser(fi.foyt.fni.rest.users.model.User user, @QueryParam ("generateCredentials") @DefaultValue ("TRUE") Boolean generateCredentials, @QueryParam ("sendNotification")  @DefaultValue ("TRUE") Boolean sendNotification) {
    return null;
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
  public Response listUsers(@QueryParam ("email") String email) {
    return null;
  }
  
  /**
   * Returns logged user info
   * 
   * @return Response response
   * @responseType fi.foyt.fni.rest.users.model.User
   */
  @Path("/users/me")
  @GET
  public Response getOwnInfo() {
    User loggedUser = sessionController.getLoggedUser();
    return Response.ok(createRestModel(loggedUser)).build();
  }
  
  private fi.foyt.fni.rest.users.model.User createRestModel(fi.foyt.fni.persistence.model.users.User user) {
    List<String> emails = userController.getUserEmails(user);
    return new fi.foyt.fni.rest.users.model.User(user.getId(), user.getFirstName(), user.getLastName(), user.getNickname(), user.getLocale(), emails);
  }

}

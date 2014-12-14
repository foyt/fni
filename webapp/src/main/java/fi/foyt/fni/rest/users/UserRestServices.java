package fi.foyt.fni.rest.users;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.users.model.UserInfo;
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
   * Returns logged user info
   * 
   * @return Response response
   * @responseType fi.foyt.fni.rest.users.model.UserInfo
   */
  @Path("/me/info")
  @GET
  public Response getOwnInfo() {
    User loggedUser = sessionController.getLoggedUser();
    return Response.ok(createRestModel(loggedUser)).build();
  }
  
  private UserInfo createRestModel(fi.foyt.fni.persistence.model.users.User user) {
    List<String> emails = userController.getUserEmails(user);
    return new UserInfo(user.getId(), user.getFirstName(), user.getLastName(), emails);
  }

}

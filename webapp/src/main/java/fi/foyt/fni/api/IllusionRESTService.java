package fi.foyt.fni.api;

import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fi.foyt.fni.persistence.dao.illusion.IllusionSessionDAO;
import fi.foyt.fni.persistence.dao.illusion.IllusionSessionParticipantDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionSession;
import fi.foyt.fni.persistence.model.illusion.IllusionSessionParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Path("/illusion")
@RequestScoped
@Stateful
@Produces ("application/json")
public class IllusionRESTService extends RESTService {

	@Inject
	private IllusionSessionDAO illusionSessionDAO;

  @Inject
  private IllusionSessionParticipantDAO illusionSessionParticipantDAO;
  
	@PUT
	@POST
	@Path ("/createSession")
	public Response createSession(
	    @FormParam ("name") String name,
			@Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders) {
			
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
		  return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		IllusionSession illusionSession = illusionSessionDAO.create(name);
		illusionSessionParticipantDAO.create(illusionSession, loggedUser, IllusionSessionParticipantRole.GAMEMASTER);

		// TODO: Not a good idea to use entity.
		return Response.status(Response.Status.OK).entity(
		  new ApiResult<IllusionSession>(illusionSession)
		).build();
	}
	
}

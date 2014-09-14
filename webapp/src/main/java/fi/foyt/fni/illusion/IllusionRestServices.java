package fi.foyt.fni.illusion;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.illusion.rest.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.session.SessionController;

@Path("/illusion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateful
@RequestScoped
public class IllusionRestServices {

  @Inject
  private SessionController sessionController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventGroupController illusionEventGroupController;
  
  @Path("/events/{EVENTURLNAME}/groups/")
  @POST
  public Response createGroup(@PathParam ("EVENTURLNAME") String eventUrlName, IllusionEventGroup entity) {
    String name = entity.getName();
    
    if (StringUtils.isBlank(name)) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(eventUrlName);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) { 
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response.ok(createRestModel(illusionEventGroupController.createGroup(event, name))).build();
  }

  private IllusionEventGroup createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventGroup group) {
    Long eventId = group.getEvent() != null ? group.getEvent().getId() : null;
    return new IllusionEventGroup(group.getId(), group.getName(), eventId);
  }

}

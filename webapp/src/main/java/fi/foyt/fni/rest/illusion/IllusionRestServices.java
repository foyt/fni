package fi.foyt.fni.rest.illusion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventGroupController;
import fi.foyt.fni.illusion.IllusionEventMaterialController;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.model.IllusionEventGroup;
import fi.foyt.fni.rest.illusion.model.IllusionEventMaterialParticipantSetting;
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
  private MaterialController materialController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventGroupController illusionEventGroupController;

  @Inject
  private IllusionEventMaterialController illusionEventMaterialController;

  @Inject
  private MaterialPermissionController materialPermissionController;
  
  /**
   * Creates new event
   * 
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEvent
   */
  @Path("/events")
  @POST
  public Response createEvent(fi.foyt.fni.rest.illusion.model.IllusionEvent entity) {
    return null;
  }
  
  /**
   * Lists events
   * 
   * @return Response
   * @responseType java.util.List<fi.foyt.fni.rest.illusion.model.IllusionEvent>
   */
  @Path("/events")
  @GET
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_LIST_EVENTS }
  )
  public Response listEvents() {
    List<IllusionEvent> events = illusionEventController.listIllusionEvents();
    if (events.isEmpty()) {
      return Response.status(Status.NO_CONTENT).build();
    }
    
    return Response.ok(createRestModel(events))
        .build();
  }

  /**
   * Returns an event
   * 
   * @param eventId event id 
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEvent
   */
  @Path("/events/{EVENTID:[0-9]*}")
  @GET
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_FIND_EVENT }
  )
  public Response getEvent(@PathParam ("EVENTID") Long eventId) {
    IllusionEvent illusionEvent = illusionEventController.findIllusionEventById(eventId);
    if (illusionEvent == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    return Response.ok(createRestModel(illusionEvent))
      .build();
  }

  /**
   * Updates an event
   * 
   * @param eventId event id 
   * @param entity payload
   * @return Response
   */
  @Path("/events/{EVENTID:[0-9]*}")
  @PUT
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_UPDATE_EVENT }
  )
  public Response updateEvent(@PathParam ("EVENTID") Long eventId, fi.foyt.fni.rest.illusion.model.IllusionEvent entity) {
    return null;
  }
  
  /**
   * Deletes an event
   * 
   * @param eventId event id 
   * @return Response
   */
  @Path("/events/{EVENTID:[0-9]*}")
  @DELETE
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_DELETE_EVENT }
  )
  public Response deleteEvent(@PathParam ("EVENTID") Long eventId) {
    User user = sessionController.getLoggedUser();
    if (user == null) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
    if (participant == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    illusionEventController.deleteIllusionEvent(event);
    
    return Response.noContent().build();
  }
  
  /**
   * Creates an event participant
   * 
   * @param eventId event id 
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventParticipant
   */
  @Path("/events/{EVENTID:[0-9]*}/participants")
  @POST
  public Response createEventParticipant(@PathParam ("EVENTID") Long eventId, fi.foyt.fni.rest.illusion.model.IllusionEventParticipant entity) {
    return null;
  }
  
  /**
   * Retrieves an event participant
   * 
   * @param eventId event id 
   * @param event participant id
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventParticipant
   */
  @Path("/events/{EVENTID:[0-9]*}/participants/{ID:[0-9]*}")
  @GET
  public Response getEventParticipant(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long participantId) {
    return null;
  }
  
  /**
   * Updates an event participant
   * 
   * @param eventId event id 
   * @param event participant id
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventParticipant
   */
  @Path("/events/{EVENTID:[0-9]*}/participants/{ID:[0-9]*}")
  @PUT
  public Response updateEventParticipant(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long participantId, fi.foyt.fni.rest.illusion.model.IllusionEventParticipant entity) {
    return null;
  }
  
  /**
   * Deletes an event participant
   * 
   * @param eventId event id 
   * @param event participant id
   * @return Response
   */
  @Path("/events/{EVENTID:[0-9]*}/participants/{ID:[0-9]*}")
  @DELETE
  public Response deleteEventParticipant(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long participantId) {
    return null;
  }
  
  /**
   * Returns list of event groups
   * 
   * @param eventId event id 
   * @return Response
   * @responseType java.util.List<fi.foyt.fni.rest.illusion.model.IllusionEventGroup>
   */
  @Path("/events/{EVENTID:[0-9]*}/groups/")
  @GET
  public Response listEventGroups(@PathParam ("EVENTID") Long eventId) {
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) { 
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response.ok(createRestModel(illusionEventGroupController.listGroups(event).toArray(new fi.foyt.fni.persistence.model.illusion.IllusionEventGroup[0]))).build();
  }
  
  /**
   * Creates an event group
   * 
   * @param eventUrlName event's urlname
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventGroup
   */
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
  
  /**
   * Creates event's material participant setting
   * 
   * @param eventId event's is
   * @param materialId material's id
   * @param participantId participant's id
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventMaterialParticipantSetting
   */
  @Path("/events/{EVENTID:[0-9]*}/materials/{MATERIALID:[0-9]*}/participantSettings/{PARTICIPANTID:[0-9]*}")
  @POST
  public Response createMaterialSetting(@PathParam ("EVENTID") Long eventId, @PathParam ("MATERIALID") Long materialId, @PathParam ("PARTICIPANTID") Long participantId, IllusionEventMaterialParticipantSetting entity) {
    if (entity.getKey() == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant loggedParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if (!isParticipantOrganizer(loggedParticipant)) {
      if (loggedParticipant == null || (!loggedParticipant.getId().equals(participant.getId()))) {
        return Response.status(Status.FORBIDDEN).build(); 
      }
    }   
    
    IllusionEventFolder illusionEventFolder = illusionEventMaterialController.getIllusionEventFolder(material);
    if (illusionEventFolder != null) {
      IllusionEvent materialEvent = illusionEventController.findIllusionEventByFolder(illusionEventFolder);
      if (materialEvent == null || (!materialEvent.getId().equals(event.getId()))) {
        return Response.status(Status.BAD_REQUEST).build();
      }
    } else {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    return Response.ok(createRestModel(illusionEventMaterialController.createParticipantSetting(material, participant, entity.getKey(), entity.getValue()))).build();
  }
  
  private boolean isParticipantOrganizer(IllusionEventParticipant participant) {
    return participant != null && participant.getRole() == IllusionEventParticipantRole.ORGANIZER;
  }
  
  /**
   * Lists event's material participant settings
   * 
   * @param eventId event's is
   * @param materialId material's id
   * @param participantId participant's id
   * @param keyName return only by key
   * @return Response
   * @responseType java.util.List<fi.foyt.fni.rest.illusion.model.IllusionEventGroup>
   */
  @Path("/events/{EVENTID:[0-9]*}/materials/{MATERIALID:[0-9]*}/participantSettings/{PARTICIPANTID:[0-9]*}")
  @GET
  public Response listMaterialSettings(@PathParam ("EVENTID") Long eventId, @PathParam ("MATERIALID") Long materialId, @PathParam ("PARTICIPANTID") Long participantId, @QueryParam ("key") String keyName) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant loggedParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if (!isParticipantOrganizer(loggedParticipant)) {
      if (loggedParticipant == null || (!loggedParticipant.getId().equals(participant.getId()))) {
        return Response.status(Status.FORBIDDEN).build(); 
      }
    }   
    
    IllusionEventFolder illusionEventFolder = illusionEventMaterialController.getIllusionEventFolder(material);
    if (illusionEventFolder != null) {
      IllusionEvent materialEvent = illusionEventController.findIllusionEventByFolder(illusionEventFolder);
      if (materialEvent == null || (!materialEvent.getId().equals(event.getId()))) {
        return Response.status(Status.BAD_REQUEST).build();
      }
    } else {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    List<fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting> result = null;
    
    if (keyName == null) {
      result = illusionEventMaterialController.listParticipantSettingByMaterialAndParticipant(material, participant);
    } else {
      IllusionEventMaterialParticipantSettingKey key = IllusionEventMaterialParticipantSettingKey.valueOf(keyName);
      if (key == null) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      
      fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting setting = illusionEventMaterialController.findParticipantSettingByMaterialAndParticipantAndKey(material, participant, key);
      if (setting == null) {
        result = Collections.emptyList();
      } else {
        result = Arrays.asList(setting);
      }
    }

    if (result.isEmpty()) {
      return Response.noContent().build();
    }
    
    return Response.ok(createRestModel(result.toArray(new fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting[0]))).build();
  }
  
  /**
   * Finds event's material participant setting
   * 
   * @param eventId event's is
   * @param materialId material's id
   * @param participantId participant's id
   * @param id participant setting id
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventMaterialParticipantSetting
   */
  @Path("/events/{EVENTID:[0-9]*}/materials/{MATERIALID:[0-9]*}/participantSettings/{PARTICIPANTID:[0-9]*}/{ID:[0-9]*}")
  @GET
  public Response getMaterialSetting(@PathParam ("EVENTID") Long eventId, @PathParam ("MATERIALID") Long materialId, @PathParam ("PARTICIPANTID") Long participantId, @PathParam ("ID") Long id) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting = illusionEventMaterialController.findParticipantSettingById(id);
    if (participantSetting == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    return Response.ok(createRestModel(participantSetting)).build();
  }
  
  /**
   * Updates event's material participant setting
   * 
   * @param eventId event's is
   * @param materialId material's id
   * @param participantId participant's id
   * @param id setting id
   * @param entity payload
   * @return Response
   */
  @Path("/events/{EVENTID:[0-9]*}/materials/{MATERIALID:[0-9]*}/participantSettings/{PARTICIPANTID:[0-9]*}/{ID:[0-9]*}")
  @PUT
  public Response updateMaterialSetting(@PathParam ("EVENTID") Long eventId, @PathParam ("MATERIALID") Long materialId, @PathParam ("PARTICIPANTID") Long participantId, @PathParam ("ID") Long id, IllusionEventMaterialParticipantSetting entity) {
    if (entity.getKey() == null) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting = illusionEventMaterialController.findParticipantSettingById(id);
    if (participantSetting == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }

    if (!participantSetting.getKey().equals(entity.getKey())) {
      return Response.status(Status.BAD_REQUEST).entity("Cannot change key in update request").build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    Material material = materialController.findMaterialById(materialId);
    if (material == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    if (!participantSetting.getMaterial().getId().equals(material.getId())) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant loggedParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if (!isParticipantOrganizer(loggedParticipant)) {
      if (loggedParticipant == null || (!loggedParticipant.getId().equals(participant.getId()))) {
        return Response.status(Status.FORBIDDEN).build(); 
      }
    }   
    
    IllusionEventFolder illusionEventFolder = illusionEventMaterialController.getIllusionEventFolder(material);
    if (illusionEventFolder != null) {
      IllusionEvent materialEvent = illusionEventController.findIllusionEventByFolder(illusionEventFolder);
      if (materialEvent == null || (!materialEvent.getId().equals(event.getId()))) {
        return Response.status(Status.BAD_REQUEST).build();
      }
    } else {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    if (!participantSetting.getParticipant().getId().equals(participant.getId())) {
      return Response.status(Status.NOT_FOUND).build(); 
    }

    return Response.ok(createRestModel(illusionEventMaterialController.updateParticipantSettingValue(participantSetting, entity.getValue()))).build();
  }
  
  /**
   * Deletes an event page 
   * 
   * @param eventId event id 
   * @param pageId page id
   * @return Response
   */
  @Path("/events/{EVENTID:[0-9]*}/pages/{PAGEID:[0-9]*}")
  @DELETE
  public Response deletePage(@PathParam ("EVENTID") Long eventId, @PathParam ("PAGEID") Long pageId) {
    if (!sessionController.isLoggedIn()) {
      return Response.status(Status.UNAUTHORIZED).build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    Material material = materialController.findMaterialById(pageId);
    if (!(material instanceof IllusionEventDocument)) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventDocument page = (IllusionEventDocument) material;
    if (page.getDocumentType() != IllusionEventDocumentType.PAGE) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventFolder illusionEventFolder = illusionEventMaterialController.getIllusionEventFolder(page);
    if (illusionEventFolder != null) {
      IllusionEvent materialEvent = illusionEventController.findIllusionEventByFolder(illusionEventFolder);
      if (materialEvent == null || (!materialEvent.getId().equals(event.getId()))) {
        return Response.status(Status.BAD_REQUEST).build();
      }
    } else {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    User loggedUser = sessionController.getLoggedUser();
    
    if (!materialPermissionController.hasModifyPermission(loggedUser, page)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    materialController.deleteMaterial(page, loggedUser);
    
    return Response.status(Status.NO_CONTENT).build();
  }
  
  private List<IllusionEventMaterialParticipantSetting> createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting... participantSettings) {
    List<IllusionEventMaterialParticipantSetting> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting : participantSettings) {
      result.add(createRestModel(participantSetting));
    }
    
    return result;
  }
  
  private IllusionEventMaterialParticipantSetting createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting participantSetting) {
    return new IllusionEventMaterialParticipantSetting(participantSetting.getId(), participantSetting.getKey(), participantSetting.getValue());
  }

  private List<IllusionEventGroup> createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventGroup... eventGroups) {
    List<IllusionEventGroup> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.illusion.IllusionEventGroup eventGroup : eventGroups) {
      result.add(createRestModel(eventGroup));
    }
    
    return result;
  }
  
  private IllusionEventGroup createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventGroup group) {
    Long eventId = group.getEvent() != null ? group.getEvent().getId() : null;
    return new IllusionEventGroup(group.getId(), group.getName(), eventId);
  }
  
  private List<fi.foyt.fni.rest.illusion.model.IllusionEvent> createRestModel(List<IllusionEvent> events) {
    List<fi.foyt.fni.rest.illusion.model.IllusionEvent> result = new ArrayList<>();
    
    for (IllusionEvent event : events) {
      result.add(createRestModel(event));
    }
    
    return result;
  }
  
  private fi.foyt.fni.rest.illusion.model.IllusionEvent createRestModel(IllusionEvent illusionEvent) {
    String signUpFeeCurrency = illusionEvent.getSignUpFeeCurrency() != null ? illusionEvent.getSignUpFeeCurrency().getCurrencyCode() : null;
    Long typeId = illusionEvent.getType() != null ? illusionEvent.getType().getId() : null;
    List<Long> genreIds = new ArrayList<>();
    DateTime signUpStartDate = getDateAsDateTime(illusionEvent.getSignUpStartDate());
    DateTime signUpEndDate = getDateAsDateTime(illusionEvent.getSignUpEndDate());
    DateTime start = getDateAndTimeAsDateTime(illusionEvent.getStartDate(), illusionEvent.getStartTime());
    DateTime end = getDateAndTimeAsDateTime(illusionEvent.getEndDate(), illusionEvent.getEndTime());
    
    return new fi.foyt.fni.rest.illusion.model.IllusionEvent(illusionEvent.getId(), illusionEvent.getName(), illusionEvent.getDescription(), 
        getDateAsDateTime(illusionEvent.getCreated()), illusionEvent.getUrlName(), illusionEvent.getXmppRoom(), illusionEvent.getJoinMode(), 
        illusionEvent.getSignUpFee(), signUpFeeCurrency, illusionEvent.getLocation(), illusionEvent.getAgeLimit(), illusionEvent.getBeginnerFriendly(),
        illusionEvent.getImageUrl(), typeId, signUpStartDate, signUpEndDate, illusionEvent.getDomain(), start, end, genreIds);
  }

  private DateTime getDateAsDateTime(Date date) {
    if (date == null) {
      return null;
    }
    
    return new DateTime(date.getTime());
  }
  
  private DateTime getDateAndTimeAsDateTime(Date date, Date time) {
    DateTime result = getDateAsDateTime(date);
    if (result == null || time == null) {
      return result;
    }
    
    result.plus(time.getTime());

    return result;
  }
  
}

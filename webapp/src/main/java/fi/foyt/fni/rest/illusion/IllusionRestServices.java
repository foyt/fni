package fi.foyt.fni.rest.illusion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventMaterialController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.registration.FormReader;
import fi.foyt.fni.materials.CharacterSheetData;
import fi.foyt.fni.materials.MaterialController;
import fi.foyt.fni.materials.MaterialPermissionController;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGenre;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.illusion.IllusionEventType;
import fi.foyt.fni.persistence.model.materials.CharacterSheet;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.oauth.OAuthClientType;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.DateTimeParameter;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.forum.model.ForumPost;
import fi.foyt.fni.rest.illusion.model.IllusionEventGroup;
import fi.foyt.fni.rest.illusion.model.IllusionEventMaterialParticipantSetting;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.time.DateTimeUtils;

@Path("/illusion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateful
@RequestScoped
public class IllusionRestServices {
  
  @Inject
  private Logger logger;

  @Inject
  private SessionController sessionController;

  @Inject
  private MaterialController materialController;

  @Inject
  private ForumController forumController;

  @Inject
  private UserController userController;

  @Inject
  private IllusionEventController illusionEventController;
  
  @Inject
  private IllusionEventMaterialController illusionEventMaterialController;

  @Inject
  private MaterialPermissionController materialPermissionController;
  
  @Inject
  private IllusionEventPageController illusionEventPageController;
  
  @Context 
  private OAuthAccessToken accessToken;
  
  /**
   * Lists genres
   * 
   * @return Response
   * @responseType java.util.List<fi.foyt.fni.rest.illusion.model.Genre>
   */
  @Path("/genres")
  @GET
  @Security (
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_LIST_GENRES }
  )
  public Response listGenres() {
    List<Genre> genres = illusionEventController.listGenres();
     
    if (genres.isEmpty()) {
      return Response.status(Status.NO_CONTENT).build();
    }
    
    return Response.ok(createRestModel(genres.toArray(new Genre[0])))
        .build();
  }
  
  /**
   * Lists types
   * 
   * @return Response
   * @responseType java.util.List<fi.foyt.fni.rest.illusion.model.EventType>
   */
  @Path("/types")
  @GET
  @Security (
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_LIST_TYPES }
  )
  public Response listTtypes() {
    List<IllusionEventType> types = illusionEventController.listTypes();
     
    if (types.isEmpty()) {
      return Response.status(Status.NO_CONTENT).build();
    }
    
    return Response.ok(createRestModel(types.toArray(new IllusionEventType[0])))
        .build();
  }
  
  /**
   * Creates new event
   * 
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEvent
   */
  @Path("/events")
  @POST
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_CREATE_EVENT }
  )
  public Response createEvent(fi.foyt.fni.rest.illusion.model.IllusionEvent entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity data is missing").build();
    }
    
    if (entity.getStart() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity start is missing").build();
    }
    
    if (entity.getEnd() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity end is missing").build();
    }
    
    if (StringUtils.isBlank(entity.getName())) {
      return Response.status(Status.BAD_REQUEST).entity("Name is required").build();
    }
    
    if (entity.getTypeId() == null) {
      return Response.status(Status.BAD_REQUEST).entity("typeId is required").build();
    }
    
    IllusionEventType type = illusionEventController.findTypeById(entity.getTypeId());
    if (type == null) {
      return Response.status(Status.BAD_REQUEST).entity("type could not be found").build();
    }
    
    if (entity.getJoinMode() == null) {
      return Response.status(Status.BAD_REQUEST).entity("joinMode could not be found").build();
    }
    
    User user = null;
    if (!sessionController.isLoggedIn()) {
      if (accessToken == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      }
      
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
      
      user = accessToken.getClient().getServiceUser();
      if (user == null) {
        return Response.status(Status.FORBIDDEN).entity("Client does not have an service user").build();
      }
    } else {
      user = sessionController.getLoggedUser();
    }
    
    Double signUpFee = entity.getSignUpFee();
    Currency signUpFeeCurrency = null;

    if (signUpFee != null && signUpFee <= 0) {
      signUpFee = null;
    }

    if (signUpFee != null) {
      signUpFeeCurrency = Currency.getInstance(entity.getSignUpFeeCurrency());
    }
    
    List<Genre> genres = null;

    if (entity.getGenreIds() != null) {
      genres = new ArrayList<>(entity.getGenreIds().size());
      for (Long genreId : entity.getGenreIds()) {
        Genre genre = illusionEventController.findGenreById(genreId);
        if (genre == null) {
          return Response.status(Status.INTERNAL_SERVER_ERROR).entity(String.format("Genre #%d could not be found", genreId)).build();
        }
        
        genres.add(genre);
      }
    } else {
      genres = Collections.emptyList();
    }
    
    Date start = toDate(entity.getStart());
    Date end = toDate(entity.getEnd());
    Date signUpStartDate = toDate(entity.getSignUpStartDate());
    Date signUpEndDate = toDate(entity.getSignUpEndDate());
    
    IllusionEvent event = illusionEventController.createIllusionEvent(user, sessionController.getLocale(), entity.getLocation(), 
        entity.getName(), entity.getDescription(), entity.getJoinMode(), new Date(), signUpFee, entity.getSignUpFeeText(), 
        signUpFeeCurrency, start, end, entity.getAgeLimit(), entity.getBeginnerFriendly(), entity.getImageUrl(), 
        type, signUpStartDate, signUpEndDate, genres);
    
    if ((entity.getPublished() != null) && (entity.getPublished())) {
      illusionEventController.publishEvent(event);
    }

    return Response.ok(createRestModel(event)).build();
  }

  private Date toDate(OffsetDateTime dateTime) {
    if (dateTime == null) {
      return null;
    }
    
    return DateTimeUtils.toDate(dateTime);
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
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_LIST_EVENTS }
  )
  public Response listEvents(
      @QueryParam ("minTime") DateTimeParameter minTime, 
      @QueryParam ("maxTime") DateTimeParameter maxTime,
      @QueryParam ("organizer") Long[] organizerIds) {
    
    List<IllusionEvent> events = null;
    List<User> organizers = null;
    
    if ((organizerIds != null) && (organizerIds.length > 0)) {
      organizers = new ArrayList<>(organizerIds.length);
      for (Long organizerId : organizerIds) {
        User organizer = userController.findUserById(organizerId);
        if ((organizer == null) || (organizer.getArchived())) {
          return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid organizer id %d", organizerId)).build();
        }
        
        organizers.add(organizer);
      }
    }
    
    if ((minTime != null) && (maxTime != null)) {
      if ((minTime == null) || (minTime == null)) {
        return Response.status(Status.BAD_REQUEST).build();
      }
      
      List<IllusionEvent> timeFrameEvents = illusionEventController.listPublishedEventsBetween(toDate(minTime.getDateTime()), toDate(maxTime.getDateTime()), Boolean.TRUE);
      
      if (organizers != null) {
        events = new ArrayList<>(timeFrameEvents.size());
        
        for (IllusionEvent timeFrameEvent : timeFrameEvents) {
          if (illusionEventController.isOneInRole(timeFrameEvent, organizers, IllusionEventParticipantRole.ORGANIZER)) {
            events.add(timeFrameEvent);
          }
        }
      } else {
        events = timeFrameEvents;
      }
    } else {
      if (organizers != null) {
        events = illusionEventController.listPublishedEventsByUsersAndRole(organizers, IllusionEventParticipantRole.ORGANIZER);
      } else {
        events = illusionEventController.listPublishedEvents();
      }
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
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity data is missing").build();
    }
    
    if (entity.getStart() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity start is missing").build();
    }
    
    if (entity.getEnd() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity end is missing").build();
    }
    
    if (entity.getPublished() == null) {
      return Response.status(Status.BAD_REQUEST).entity("Entity published is missing").build();
    }
    
    if (StringUtils.isBlank(entity.getName())) {
      return Response.status(Status.BAD_REQUEST).entity("Name is required").build();
    }
    
    if (entity.getTypeId() == null) {
      return Response.status(Status.BAD_REQUEST).entity("typeId is required").build();
    }
    
    IllusionEventType type = illusionEventController.findTypeById(entity.getTypeId());
    if (type == null) {
      return Response.status(Status.BAD_REQUEST).entity("type could not be found").build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if (StringUtils.isNotBlank(entity.getXmppRoom()) && !StringUtils.equals(entity.getXmppRoom(), event.getXmppRoom())) {
      return Response.status(Status.BAD_REQUEST).entity("XmppRoom can not be changed").build();
    }
    
    if (StringUtils.isNotBlank(entity.getDomain())) {
      if (!illusionEventController.isEventAllowedDomain(entity.getDomain())) {
        return Response.status(Status.BAD_REQUEST).entity(String.format("Invalid domain name: %s", entity.getDomain())).build();
      } 
    }

    User user = null;
    if (!sessionController.isLoggedIn()) {
      if (accessToken == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      }
      
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
      
      user = accessToken.getClient().getServiceUser();
      if (user == null) {
        return Response.status(Status.FORBIDDEN).entity("Client does not have an service user").build();
      }
    } else {
      user = sessionController.getLoggedUser();
    }
    
    Double signUpFee = entity.getSignUpFee();
    Currency signUpFeeCurrency = null;
    String signUpFeeText = entity.getSignUpFeeText();

    if (signUpFee != null && signUpFee <= 0) {
      signUpFee = null;
    }

    if ((signUpFee != null) || (StringUtils.isNotBlank(signUpFeeText))) {
      signUpFeeCurrency = Currency.getInstance(entity.getSignUpFeeCurrency());
    }
    
    List<Genre> genres = new ArrayList<>(entity.getGenreIds().size());
    for (Long genreId : entity.getGenreIds()) {
      Genre genre = illusionEventController.findGenreById(genreId);
      if (genre == null) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(String.format("Genre #%d could not be found", genreId)).build();
      }
      
      genres.add(genre);
    }

    illusionEventController.updateIllusionEventName(event, entity.getName());
    illusionEventController.updateIllusionEventDescription(event, entity.getDescription());
    illusionEventController.updateIllusionEventJoinMode(event, entity.getJoinMode());
    illusionEventController.updateIllusionEventStart(event, toDate(entity.getStart()));
    illusionEventController.updateIllusionEventEnd(event, toDate(entity.getEnd()));
    illusionEventController.updateIllusionEventLocation(event, entity.getLocation());
    illusionEventController.updateIllusionEventType(event, type);
    illusionEventController.updateIllusionEventSignUpTimes(event, toDate(entity.getSignUpStartDate()), toDate(entity.getSignUpEndDate()));
    illusionEventController.updateIllusionEventAgeLimit(event, entity.getAgeLimit());
    illusionEventController.updateIllusionEventBeginnerFriendly(event, entity.getBeginnerFriendly());
    illusionEventController.updateIllusionEventImageUrl(event, entity.getImageUrl());
    illusionEventController.updateEventGenres(event, genres);
    illusionEventController.updateEventDomain(event, entity.getDomain());
    illusionEventController.updateEventSignUpFee(event, signUpFeeText, signUpFee, signUpFeeCurrency, event.getPaymentMode());
    illusionEventController.updatePublished(event, entity.getPublished());
    
    return Response.noContent().build();
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
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_CREATE_EVENT_PARTICIPANT }
  )
  public Response createEventParticipant(@PathParam ("EVENTID") Long eventId, fi.foyt.fni.rest.illusion.model.IllusionEventParticipant entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).entity("Payload is missing").build();
    }
    
    if (entity.getUserId() == null) {
      return Response.status(Status.BAD_REQUEST).entity("userId is missing").build();
    }
    
    User user = userController.findUserById(entity.getUserId());
    if (user == null) {
      return Response.status(Status.BAD_REQUEST).entity("userId points to a non-existing user").build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (illusionEventController.findIllusionEventParticipantByEventAndUser(event, user) != null) {
      return Response.status(Status.BAD_REQUEST).entity("User is already a participant in this event").build();
    }
    
    IllusionEventParticipant participant = null;
    if (entity.getDisplayName() == null) {
      participant = illusionEventController.createIllusionEventParticipant(user, event, entity.getDisplayName(), entity.getRole());
    } else {
      participant = illusionEventController.createIllusionEventParticipant(user, event, entity.getRole());
    }
    
    if ((accessToken == null) || (accessToken.getClient().getType() != OAuthClientType.SERVICE)) {
      if (sessionController.isLoggedIn()) {
        if (!isLoggedUserEventOrganizer(event)) {
          return Response.status(Status.FORBIDDEN).build();
        }
      } else {
        return Response.status(Status.UNAUTHORIZED).build();
      }
    } 
    
    return Response.ok(createRestModel(participant)).build();
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
  @Security (
    allowService = true,
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_FIND_EVENT_PARTICIPANT }
  )
  public Response getEventParticipant(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long participantId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!participant.getEvent().getId().equals(event.getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!event.getPublished()) {
      if ((accessToken == null) || (accessToken.getClient().getType() != OAuthClientType.SERVICE)) {
        if (sessionController.isLoggedIn()) {
          if (!isLoggedUserEventOrganizer(event)) {
            return Response.status(Status.FORBIDDEN).build();
          }
        } else {
          return Response.status(Status.UNAUTHORIZED).build();
        }
      }      
    }
    
    return Response.ok(createRestModel(participant)).build();
  }

  /**
   * Lists event participants
   * 
   * @param eventId event id 
   * @param userId filter results by user id
   * @return Response
   * @responseType java.util.List<fi.foyt.fni.rest.illusion.model.IllusionEventParticipant>
   */
  @Path("/events/{EVENTID:[0-9]*}/participants/")
  @GET
  @Security (
    allowService = true,
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_FIND_EVENT_PARTICIPANT }
  )
  public Response listEventParticipants(@PathParam ("EVENTID") Long eventId, @QueryParam ("userId") Long userId, @QueryParam ("email") String email) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!event.getPublished()) {
      if ((accessToken == null) || (accessToken.getClient().getType() != OAuthClientType.SERVICE)) {
        if (sessionController.isLoggedIn()) {
          if (!isLoggedUserEventOrganizer(event)) {
            return Response.status(Status.FORBIDDEN).build();
          }
        } else {
          return Response.status(Status.UNAUTHORIZED).build();
        }
      }
    }
    
    List<IllusionEventParticipant> result = null;
    User user = null;
    
    if (userId != null) {
      user = userController.findUserById(userId);
    } else if (StringUtils.isNotBlank(email)) {
      user = userController.findUserByEmail(email);
    }
    
    if (user != null) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
      if (participant != null) {
        result = Arrays.asList(participant);
      }
    }
    else {
      result = illusionEventController.listIllusionEventParticipantsByEvent(event);
    }
    
    if (result == null || (result.isEmpty())) {
      return Response.noContent().build();
    }
    
    return Response.ok(createRestModel(result.toArray(new IllusionEventParticipant[0]))).build();
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
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_UPDATE_EVENT_PARTICIPANT }
  )
  public Response updateEventParticipant(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long participantId, fi.foyt.fni.rest.illusion.model.IllusionEventParticipant entity) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!participant.getEvent().getId().equals(event.getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if ((accessToken == null) || (accessToken.getClient().getType() != OAuthClientType.SERVICE)) {
      if (sessionController.isLoggedIn()) {
        if (!isLoggedUserEventOrganizer(event)) {
          return Response.status(Status.FORBIDDEN).build();
        }
      } else {
        return Response.status(Status.UNAUTHORIZED).build();
      }
    } 
    
    illusionEventController.updateIllusionEventParticipantRole(participant, entity.getRole());
    illusionEventController.updateIllusionEventParticipantDisplayName(participant, entity.getDisplayName());
    
    return Response.noContent().build();
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
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_DELETE_EVENT_PARTICIPANT }
  )
  public Response deleteEventParticipant(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long participantId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantById(participantId);
    if (participant == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!participant.getEvent().getId().equals(event.getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }

    if ((accessToken == null) || (accessToken.getClient().getType() != OAuthClientType.SERVICE)) {
      if (sessionController.isLoggedIn()) {
        if (!isLoggedUserEventOrganizer(event)) {
          return Response.status(Status.FORBIDDEN).build();
        }
      } else {
        return Response.status(Status.UNAUTHORIZED).build();
      }
    } 
    
    illusionEventController.deleteParticipant(participant);
    
    return Response.noContent().build();
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
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_GROUP_LIST }
  )
  public Response listEventGroups(@PathParam ("EVENTID") Long eventId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    if (!sessionController.isLoggedIn()) {
      if (accessToken == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      }
      
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
    } else {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
      if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) { 
        return Response.status(Status.FORBIDDEN).build();
      }
    }
    
    List<fi.foyt.fni.persistence.model.illusion.IllusionEventGroup> groups = illusionEventController.listGroups(event);
    
    return Response.ok(createRestModel(groups.toArray(new fi.foyt.fni.persistence.model.illusion.IllusionEventGroup[0]))).build();
  }
  
  /**
   * Returns an event group
   * 
   * @param eventId event id 
   * @param groupId group id 
   * @return Response
   * @responseType fi.foyt.fni.rest.illusion.model.IllusionEventGroup
   */
  @Path("/events/{EVENTID:[0-9]*}/groups/{ID:[0-9]*}")
  @GET
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_GROUP_LIST }
  )
  public Response findEventGroup(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long groupId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    if (!sessionController.isLoggedIn()) {
      if (accessToken == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      }
      
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
    } else {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
      if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) { 
        return Response.status(Status.FORBIDDEN).build();
      }
    }
    
    fi.foyt.fni.persistence.model.illusion.IllusionEventGroup group = illusionEventController.findGroupById(groupId);
    if (group == null) {
      return Response.status(Status.NOT_FOUND).entity(String.format("Group %d not found", groupId)).build();
    }
    
    if (group.getEvent().equals(eventId)) {
      return Response.status(Status.NOT_FOUND).entity(String.format("Group %d does not belong to event %d", groupId, eventId)).build();
    }
    
    return Response.ok(createRestModel(group)).build();
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
  @Security (
    allowService = true,
    scopes = { OAuthScopes.ILLUSION_CREATE_GROUP }
  )
  public Response createGroup(@PathParam ("EVENTURLNAME") String eventUrlName, IllusionEventGroup entity) {
    String name = entity.getName();
    
    if (StringUtils.isBlank(name)) {
      return Response.status(Status.BAD_REQUEST).build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventByUrlName(eventUrlName);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    User user = null;
    
    if (!sessionController.isLoggedIn()) {
      if (accessToken == null) {
        return Response.status(Status.UNAUTHORIZED).build();
      }
      
      if (accessToken.getClient().getType() != OAuthClientType.SERVICE) {
        return Response.status(Status.FORBIDDEN).entity(String.format("Invalid client type %s", accessToken.getClient().getType().toString())).build();
      }
      
      user = accessToken.getClient().getServiceUser();
    } else {
      user = sessionController.getLoggedUser();
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
      if ((participant == null) || (participant.getRole() != IllusionEventParticipantRole.ORGANIZER)) { 
        return Response.status(Status.FORBIDDEN).build();
      }
    }

    return Response.ok(createRestModel(illusionEventController.createGroup(event, name, user))).build();
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
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_CREATE_MATERIAL_PARTICIPANT_SETTING }
  )
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
  
  private boolean isLoggedUserEventOrganizer(IllusionEvent event) {
    if (sessionController.isLoggedIn()) {
      IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
      if (participant != null) {
        return participant.getRole() == IllusionEventParticipantRole.ORGANIZER;
      }
    }
    
    return false;
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
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_LIST_MATERIAL_PARTICIPANT_SETTING }
  )
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
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_FIND_MATERIAL_PARTICIPANT_SETTING }
  )
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
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_UPDATE_MATERIAL_PARTICIPANT_SETTING }
  )
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
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_DELETE_EVENT_PAGE }
  )
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
  
  /**
   * Creates Illusion event forum post 
   * 
   * @param eventId event id
   * @param entity payload
   * @return Response
   */  
  @POST
  @Path("/events/{EVENTID:[0-9]*}/forumPosts/")
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_CREATE_FORUM_POST }
  )
  public Response createForumPost(@PathParam ("EVENTID") Long eventId, ForumPost entity) {
    if (entity == null) {
      return Response.status(Status.BAD_REQUEST).entity("Payload missing").build();
    }
    
    if (StringUtils.isBlank(entity.getContent())) {
      return Response.status(Status.BAD_REQUEST).entity("Content missing").build();
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    User user = sessionController.getLoggedUser();

    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
    if (participant == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if ((participant.getRole() != IllusionEventParticipantRole.ORGANIZER) && (participant.getRole() != IllusionEventParticipantRole.PARTICIPANT)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    return Response
      .ok(createRestModel(forumController.createForumPost(event.getForumTopic(), user, entity.getContent())))
      .build(); 
  }
  
  /**
   * Lists forum event post
   * 
   * @param eventId event id
   * @return Response
   */ 
  @GET
  @Path("/events/{EVENTID:[0-9]*}/forumPosts/")
  @Security (
    allowService = false,
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_LIST_FORUM_POSTS }
  )
  public Response listForumPosts(@PathParam ("EVENTID") Long eventId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }

    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if (!illusionEventPageController.isPageVisible(participant, event, IllusionEventPage.Static.FORUM)) {
      if (!sessionController.isLoggedIn()) {
        return Response.status(Status.UNAUTHORIZED).build();
      } else {
        return Response.status(Status.FORBIDDEN).build(); 
      }
    }
     
    List<fi.foyt.fni.persistence.model.forum.ForumPost> posts = forumController.listPostsByTopic(event.getForumTopic());
    if (posts.isEmpty()) {
      return Response.noContent().build();
    }
    
    return Response
      .ok(createRestModel(posts.toArray(new fi.foyt.fni.persistence.model.forum.ForumPost[0])))
      .build(); 
  }
  
  /**
   * Finds forum event post
   * 
   * @param eventId event id
   * @param postId post id to be updated
   * @return Response
   */
  @GET
  @Path("/events/{EVENTID:[0-9]*}/forumPosts/{POSTID:[0-9]*}")
  @Security (
    allowService = false,
    allowNotLogged = true,
    scopes = { OAuthScopes.ILLUSION_FIND_FORUM_POST }
  )
  public Response findForumPost(@PathParam ("EVENTID") Long eventId, @PathParam ("POSTID") Long postId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, sessionController.getLoggedUser());
    if (!illusionEventPageController.isPageVisible(participant, event, IllusionEventPage.Static.FORUM)) {
      if (!sessionController.isLoggedIn()) {
        return Response.status(Status.UNAUTHORIZED).build();
      } else {
        return Response.status(Status.FORBIDDEN).build(); 
      }
    }

    fi.foyt.fni.persistence.model.forum.ForumPost post = forumController.findForumPostById(postId);
    if ((post == null) || (!post.getTopic().getId().equals(event.getForumTopic().getId()))) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    return Response.ok(createRestModel(post)).build();
  }
  
  /**
   * Updates forum post event
   * 
   * @param eventId event id
   * @param postId post id to be updated
   * @param entity update payload
   * @return Response
   */
  @PUT
  @Path("/events/{EVENTID:[0-9]*}/forumPosts/{POSTID:[0-9]*}")
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_UPDATE_FORUM_POST }
  )
  public Response updateForumPost(@PathParam ("EVENTID") Long eventId, @PathParam ("POSTID") Long postId, ForumPost entity) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    fi.foyt.fni.persistence.model.forum.ForumPost post = forumController.findForumPostById(postId);
    if ((post == null) || (!post.getTopic().getId().equals(event.getForumTopic().getId()))) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
      
    User user = sessionController.getLoggedUser();

    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
    if (participant == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if ((participant.getRole() != IllusionEventParticipantRole.ORGANIZER) && (participant.getRole() != IllusionEventParticipantRole.PARTICIPANT)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if (!post.getAuthor().getId().equals(user.getId())) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    forumController.updateForumPostContent(post, entity.getContent());
    
    return Response.noContent().build();
  }
  
  /**
   * Removes forum post from event
   * 
   * @param eventId event id
   * @param postId post id to be removed
   * @return Response
   */
  @DELETE
  @Path("/events/{EVENTID:[0-9]*}/forumPosts/{POSTID:[0-9]*}")
  @Security (
    allowService = false,
    scopes = { OAuthScopes.ILLUSION_DELETE_FORUM_POST }
  )
  public Response deleteForumPost(@PathParam ("EVENTID") Long eventId, @PathParam ("POSTID") Long postId) {
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    fi.foyt.fni.persistence.model.forum.ForumPost post = forumController.findForumPostById(postId);
    if ((post == null) || (!post.getTopic().getId().equals(event.getForumTopic().getId()))) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
      
    User user = sessionController.getLoggedUser();

    IllusionEventParticipant participant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, user);
    if (participant == null) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if ((participant.getRole() != IllusionEventParticipantRole.ORGANIZER) && (participant.getRole() != IllusionEventParticipantRole.PARTICIPANT)) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    if (participant.getRole() == IllusionEventParticipantRole.PARTICIPANT && !post.getAuthor().getId().equals(user.getId())) {
      return Response.status(Status.FORBIDDEN).build();
    }

    forumController.deletePost(post);

    return Response.noContent().build();
  }
  
  /**
   * Returns character sheet data
   * 
   * @param eventId id of event
   * @param characterSheetId id of character sheet
   * @param format output format
   * @return Response
   */
  @GET
  @Path("/events/{EVENTID:[0-9]*}/characterSheets/{ID:[0-9]*}/data")
  @Security (
    allowService = false,
    allowNotLogged = false,
    scopes = { OAuthScopes.ILLUSION_FIND_CHARACTER_SHEET_DATA }
  )
  public Response getCharacterSheetData(@PathParam ("EVENTID") Long eventId, @PathParam ("ID") Long characterSheetId, @QueryParam ("format") String format) {
    if (format == null) {
      return Response.status(Status.BAD_REQUEST).entity("format is required").build(); 
    }
    
    DataOutputFormat outputFormat = DataOutputFormat.valueOf(format);
    if (outputFormat == null) {
      return Response.status(Status.BAD_REQUEST).entity("invalid format").build(); 
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    if (!isLoggedUserEventOrganizer(event)) {
      return Response.status(Status.FORBIDDEN).entity("Only event organizers can export character sheet data").build(); 
    }
    
    CharacterSheet characterSheet = materialController.findCharacterSheetById(characterSheetId);
    
    IllusionEventFolder sheetEventFolder = illusionEventMaterialController.getIllusionEventFolder(characterSheet);
    if (sheetEventFolder != null) {
      IllusionEvent sheetEvent = illusionEventController.findIllusionEventByFolder(sheetEventFolder);
      if (sheetEvent == null || (!sheetEvent.getId().equals(event.getId()))) {
        return Response.status(Status.NOT_FOUND).build();
      }
    } else {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    try {
      switch (outputFormat) {
        case XLS:
          return Response.ok(getCharacterSheetDataAsXLS(characterSheet)).header("Content-Disposition", "attachment; filename=" + characterSheet.getUrlName() + ".xls").build();
      }
      
      return Response.status(Status.BAD_REQUEST).entity("invalid format").build(); 
    } catch (IOException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  private byte[] getCharacterSheetDataAsXLS(CharacterSheet characterSheet) throws JsonParseException, JsonMappingException, IOException {
    Workbook workbook = new HSSFWorkbook();
    try {
      CharacterSheetData sheetData = materialController.getCharacterSheetData(characterSheet);
      List<String> keys = sheetData.getSortedEntryNames();

      Sheet summarySheet = workbook.createSheet("Summary");
      List<String> sheetNames = new ArrayList<>();
      
      for (Long userId : sheetData.getUserIds()) {
        User user = userController.findUserById(userId);
        Sheet sheet = workbook.createSheet(userController.getUserPrimaryEmail(user));
        sheetNames.add(sheet.getSheetName());
        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Key");
        headerRow.createCell(1).setCellValue("Value");

        for (int i = 0, l = keys.size(); i < l; i++) {
          String key = keys.get(i);

          Row row = sheet.createRow(i + 1);
          row.createCell(0).setCellValue(key);
          switch (sheetData.getDataType(key)) {
            case NUMBER:
              Double doubleValue = sheetData.getDouble(key, userId);
              if (doubleValue != null) {
                row.createCell(1).setCellValue(doubleValue);
              }
            break;
            default:
              String textValue = sheetData.getText(key, userId);
              row.createCell(1).setCellValue(textValue);
            break;
          }
        }
      }
      
      Row summaryHeaderRow = summarySheet.createRow(0);

      summaryHeaderRow.createCell(0).setCellValue("Key");
      summaryHeaderRow.createCell(1).setCellValue("Min");
      summaryHeaderRow.createCell(2).setCellValue("Max");
      summaryHeaderRow.createCell(3).setCellValue("Average");
      
      for (int i = 0, l = keys.size(); i < l; i++) {
        String key = keys.get(i);

        Row row = summarySheet.createRow(i + 1);
        row.createCell(0).setCellValue(key);
        
        List<String> refs = new ArrayList<>();
        for (String sheetName : sheetNames) {
          CellReference reference = new CellReference(sheetName, i + 1, 1, false, false);
          refs.add(reference.formatAsString());
        }
        
        String cellRefs = StringUtils.join(refs.toArray(new String[0]), ',');
        
        row.createCell(1).setCellFormula("MIN(" + cellRefs + ")");
        row.createCell(2).setCellFormula("MAX(" + cellRefs + ")");
        row.createCell(3).setCellFormula("AVERAGE(" + cellRefs + ")");
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
        workbook.write(out);
        out.flush();
        return out.toByteArray();
        
      } finally {
        out.close();
      }

    } finally {
      workbook.close();
    }
  }
  
  /**
   * Returns registration for data
   * 
   * @param eventId id of event
   * @param format output format
   * @return Response
   */
  @GET
  @Path("/events/{EVENTID:[0-9]*}/registrationFormData")
  @Security (
    allowService = false,
    allowNotLogged = false,
    scopes = { OAuthScopes.ILLUSION_FIND_REGISTRATION_FORM_DATA }
  )
  public Response getRegistrationFormData(@PathParam ("EVENTID") Long eventId, @QueryParam ("format") String format) {
    if (format == null) {
      return Response.status(Status.BAD_REQUEST).entity("format is required").build(); 
    }
    
    DataOutputFormat outputFormat = DataOutputFormat.valueOf(format);
    if (outputFormat == null) {
      return Response.status(Status.BAD_REQUEST).entity("invalid format").build(); 
    }
    
    IllusionEvent event = illusionEventController.findIllusionEventById(eventId);
    if (event == null) {
      return Response.status(Status.NOT_FOUND).build(); 
    }
    
    if (!isLoggedUserEventOrganizer(event)) {
      return Response.status(Status.FORBIDDEN).entity("Only event organizers can export registration form datas").build(); 
    }
    
    IllusionEventRegistrationForm registrationForm = illusionEventController.findEventRegistrationForm(event);
    
    try {
      switch (outputFormat) {
        case XLS:
          return Response
              .ok(getRegistrationSheetDataAsXLS(event, registrationForm))
              .header("Content-Disposition", String.format("attachment; filename=%s_registrations.xls", event.getUrlName()))
              .build();
      }
      
      return Response.status(Status.BAD_REQUEST).entity("invalid format").build(); 
    } catch (IOException e) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  private byte[] getRegistrationSheetDataAsXLS(IllusionEvent event, IllusionEventRegistrationForm registrationForm) throws JsonParseException, JsonMappingException, IOException {
    FormReader formReader = registrationForm != null ? new FormReader(registrationForm.getData()) : null;
    Locale locale = sessionController.getLocale();
    List<String> fields = null;
    List<IllusionEventParticipant> participants = illusionEventController.listIllusionEventParticipantsByEventAndRole(event, IllusionEventParticipantRole.PARTICIPANT);
    
    Workbook workbook = new HSSFWorkbook();
    try {
      Sheet sheet = workbook.createSheet(event.getName());
      Row headerRow = sheet.createRow(0);
      headerRow.createCell(0).setCellValue(ExternalLocales.getText(locale, "illusion.registration.registeredHeader"));
      
      if (registrationForm != null) {
        fields = formReader.getFields(true);
        for (int i = 0, l = fields.size(); i < l; i++) {
          String fieldLabel = formReader.getFieldLabel(fields.get(i));
          Cell cell = headerRow.createCell(i + 1);
          cell.setCellValue(fieldLabel);
        }
      } else {
        headerRow.createCell(1).setCellValue(ExternalLocales.getText(locale, "illusion.registration.defaultEmailHeader"));
        headerRow.createCell(2).setCellValue(ExternalLocales.getText(locale, "illusion.registration.defaultFirstNameHeader"));
        headerRow.createCell(3).setCellValue(ExternalLocales.getText(locale, "illusion.registration.defaultLastNameHeader"));
      }
      
      SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.FULL, locale);
      CreationHelper createHelper = workbook.getCreationHelper();
      CellStyle dateStyle = workbook.createCellStyle();
      dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat.toPattern()));
      
      for (int participantIndex = 0, participantsSize = participants.size(); participantIndex < participantsSize; participantIndex++) {
        IllusionEventParticipant participant = participants.get(participantIndex);
        Row row = sheet.createRow(participantIndex + 1);
        Cell createdCell = row.createCell(0);
        createdCell.setCellStyle(dateStyle);
        createdCell.setCellValue(participant.getCreated());
        
        if (registrationForm != null) {
          Map<String, String> answers = illusionEventController.loadRegistrationFormAnswers(registrationForm, participant);
          
          for (int fieldIndex = 0, fieldsSize = fields.size(); fieldIndex < fieldsSize; fieldIndex++) {
            String value = answers.get(fields.get(fieldIndex));
            Cell cell = row.createCell(fieldIndex + 1);
            cell.setCellValue(value);
          }
        } else {
          User user = participant.getUser();
          if (user != null) {
            row.createCell(1).setCellValue(userController.getUserPrimaryEmail(user));
            row.createCell(2).setCellValue(user.getFirstName());
            row.createCell(3).setCellValue(user.getLastName());
          } else {
            logger.log(Level.SEVERE, String.format("Participant %d had a null user", participant.getId()));
          }
        }
      }

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
        workbook.write(out);
        out.flush();
        return out.toByteArray();
        
      } finally {
        out.close();
      }

    } finally {
      workbook.close();
    }
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
    OffsetDateTime signUpStartDate = getDateAsDateTime(illusionEvent.getSignUpStartDate());
    OffsetDateTime signUpEndDate = getDateAsDateTime(illusionEvent.getSignUpEndDate());
    String signUpFeeText = illusionEvent.getSignUpFeeText();
    
    List<IllusionEventGenre> genres = illusionEventController.listIllusionEventGenres(illusionEvent);
    
    List<Long> genreIds = new ArrayList<>(genres.size());
    for (IllusionEventGenre genre : genres) {
      genreIds.add(genre.getGenre().getId());
    }
    
    OffsetDateTime start = getDateAsDateTime(illusionEvent.getStart());
    OffsetDateTime end = getDateAsDateTime(illusionEvent.getEnd());
    
    return new fi.foyt.fni.rest.illusion.model.IllusionEvent(illusionEvent.getId(), illusionEvent.getPublished(), illusionEvent.getName(), illusionEvent.getDescription(), 
        getDateAsDateTime(illusionEvent.getCreated()), illusionEvent.getUrlName(), illusionEvent.getXmppRoom(), illusionEvent.getJoinMode(), signUpFeeText,
        illusionEvent.getSignUpFee(), signUpFeeCurrency, illusionEvent.getLocation(), illusionEvent.getAgeLimit(), illusionEvent.getBeginnerFriendly(),
        illusionEvent.getImageUrl(), typeId, signUpStartDate, signUpEndDate, illusionEvent.getDomain(), start, end, genreIds);
  }
  
  private List<fi.foyt.fni.rest.illusion.model.IllusionEventParticipant> createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant... participants) {
    List<fi.foyt.fni.rest.illusion.model.IllusionEventParticipant> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant participant : participants) {
      result.add(createRestModel(participant));
    }
    
    return result;
  }

  private fi.foyt.fni.rest.illusion.model.IllusionEventParticipant createRestModel(IllusionEventParticipant participant) {
    return new fi.foyt.fni.rest.illusion.model.IllusionEventParticipant(participant.getId(), participant.getUser().getId(), participant.getRole(), participant.getDisplayName());
  }
  
  private OffsetDateTime getDateAsDateTime(Date date) {
    if (date == null) {
      return null;
    }
    
    return DateTimeUtils.toOffsetDateTime(date);
  }
  
  private ForumPost[] createRestModel(fi.foyt.fni.persistence.model.forum.ForumPost... forumPosts) {
    List<ForumPost> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.forum.ForumPost forumPost : forumPosts) {
      result.add(createRestModel(forumPost));
    }
    
    return result.toArray(new ForumPost[0]);
  }
  
  private fi.foyt.fni.rest.illusion.model.Genre[] createRestModel(fi.foyt.fni.persistence.model.illusion.Genre... genres) {
    List<fi.foyt.fni.rest.illusion.model.Genre> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.illusion.Genre genre : genres) {
      result.add(createRestModel(genre));
    }
    
    return result.toArray(new fi.foyt.fni.rest.illusion.model.Genre[0]);
  }
  
  private fi.foyt.fni.rest.illusion.model.Genre createRestModel(fi.foyt.fni.persistence.model.illusion.Genre genre) {
    return new fi.foyt.fni.rest.illusion.model.Genre(genre.getId(), genre.getName());
  }
  
  private fi.foyt.fni.rest.illusion.model.EventType[] createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventType... types) {
    List<fi.foyt.fni.rest.illusion.model.EventType> result = new ArrayList<>();
    
    for (fi.foyt.fni.persistence.model.illusion.IllusionEventType type : types) {
      result.add(createRestModel(type));
    }
    
    return result.toArray(new fi.foyt.fni.rest.illusion.model.EventType[0]);
  }
  
  private fi.foyt.fni.rest.illusion.model.EventType createRestModel(fi.foyt.fni.persistence.model.illusion.IllusionEventType type) {
    return new fi.foyt.fni.rest.illusion.model.EventType(type.getId(), type.getName());
  }
  
  private ForumPost createRestModel(fi.foyt.fni.persistence.model.forum.ForumPost forumPost) {
    OffsetDateTime modified = getDateAsDateTime(forumPost.getModified());
    OffsetDateTime created = getDateAsDateTime(forumPost.getCreated());
    
    return new ForumPost(forumPost.getId(), forumPost.getTopic().getId(), forumPost.getContent(), 
        modified, created, forumPost.getAuthor().getId(), forumPost.getViews()); 
  }
  
  private enum DataOutputFormat {
    XLS
  }
  
}

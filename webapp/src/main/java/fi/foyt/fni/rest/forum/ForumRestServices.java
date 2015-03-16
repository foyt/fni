package fi.foyt.fni.rest.forum;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.forum.ForumTopicWatcher;
import fi.foyt.fni.persistence.model.oauth.OAuthAccessToken;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.rest.Security;
import fi.foyt.fni.rest.illusion.OAuthScopes;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@Path("/forum")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Stateful
@RequestScoped
public class ForumRestServices {

  @Inject
  private SessionController sessionController;

  @Inject
  private ForumController forumController;

  @Inject
  private UserController userController;
  
  @Context 
  private OAuthAccessToken accessToken;
  
  /**
   * Creates forum topic watcher
   * 
   * @param forumId id of forum
   * @param topicId id of topic
   * @param entity payload
   * @return Response
   * @responseType fi.foyt.fni.rest.forum.model.ForumTopicWatcher
   */
  @Path("/forums/{FORUMID:[0-9]*}/topics/{TOPICID:[0-9]*}/watchers/")
  @POST
  @Security (
    allowService = false,
    allowNotLogged = false,
    scopes = { OAuthScopes.FORUM_CREATE_WATCHER }
  )
  public Response createTopicWatcher(@PathParam ("FORUMID") Long forumId, @PathParam ("TOPICID") Long topicId, fi.foyt.fni.rest.forum.model.ForumTopicWatcher entity) {
    if (entity.getUserId() == null) {
      return Response.status(Status.BAD_REQUEST).entity("userId is required").build();
    }
    
    Forum forum = forumController.findForumById(forumId);
    if (forum == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    ForumTopic topic = forumController.findForumTopicById(topicId);
    if (topic == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!topic.getForum().getId().equals(forum.getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!sessionController.getLoggedUserId().equals(entity.getUserId())) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    User user = userController.findUserById(entity.getUserId());
    
    if (forumController.isWatchingTopic(user, topic)) {
      return Response.status(Status.BAD_REQUEST).entity("Already watching this topic").build();
    }
    
    ForumTopicWatcher topicWatcher = forumController.createTopicWatcher(user, topic);

    return Response.ok(createRestModel(topicWatcher)).build();
  }
  
  /**
   * Lists forum topic watchers
   * 
   * @param forumId id of forum
   * @param topicId id of topic
   * @param userId filters results by user id
   * @return Response
   * @responseType fi.foyt.fni.rest.forum.model.ForumTopicWatcher
   */
  @Path("/forums/{FORUMID:[0-9]*}/topics/{TOPICID:[0-9]*}/watchers/")
  @GET
  @Security (
    allowService = false,
    allowNotLogged = false,
    scopes = { OAuthScopes.FORUM_LIST_WATCHERS }
  )
  public Response listTopicWatchers(@PathParam ("FORUMID") Long forumId, @PathParam ("TOPICID") Long topicId, @QueryParam ("userId") Long userId) {
    Forum forum = forumController.findForumById(forumId);
    if (forum == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    ForumTopic topic = forumController.findForumTopicById(topicId);
    if (topic == null) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (!topic.getForum().getId().equals(forum.getId())) {
      return Response.status(Status.NOT_FOUND).build();
    }
    
    if (userId == null) {
      return Response.status(Status.NOT_IMPLEMENTED).entity("Listing watchers without userId parameter is currently unsupported").build();
    } else {
      if (!sessionController.getLoggedUserId().equals(userId)) {
        return Response.status(Status.FORBIDDEN).build();
      }
      
      User user = userController.findUserById(userId);
      ForumTopicWatcher watcher = forumController.findTopicWatcherByUserAndTopic(user, topic);
      if (watcher == null) {
        return Response.noContent().build();
      }
      
      return Response.ok(createRestModel(new ForumTopicWatcher[] { watcher } )).build();
    }
  }
  
  /**
   * Removes forum topic watcher
   * 
   * @param forumId id of forum
   * @param topicId id of topic
   * @param id id of watcher to be removed
   * @return Response
   * @responseType fi.foyt.fni.rest.forum.model.ForumTopicWatcher
   */
  @Path("/forums/{FORUMID:[0-9]*}/topics/{TOPICID:[0-9]*}/watchers/{ID:[0-9]*}")
  @DELETE
  @Security (
    allowService = false,
    allowNotLogged = false,
    scopes = { OAuthScopes.FORUM_DELETE_WATCHER }
  )
  public Response deleteTopicWatcher(@PathParam ("FORUMID") Long forumId, @PathParam ("TOPICID") Long topicId, @PathParam ("ID") Long id) {
    Forum forum = forumController.findForumById(forumId);
    if (forum == null) {
      return Response.status(Status.NOT_FOUND).entity("Forum could not be found").build();
    }
    
    ForumTopic topic = forumController.findForumTopicById(topicId);
    if (topic == null) {
      return Response.status(Status.NOT_FOUND).entity("Topic could not be found").build();
    }
    
    if (!topic.getForum().getId().equals(forum.getId())) {
      return Response.status(Status.NOT_FOUND).entity("Topic could not be found from forum").build();
    }
    
    ForumTopicWatcher watcher = forumController.findTopicWatcherById(id);
    if (watcher == null) {
      return Response.status(Status.NOT_FOUND).entity("Watcher could not be found").build();
    }
    
    if (!watcher.getTopic().getId().equals(topic.getId())) {
      return Response.status(Status.NOT_FOUND).entity("Watcher could not be found from topic").build();
    }
    
    if (!sessionController.getLoggedUserId().equals(watcher.getUser().getId())) {
      return Response.status(Status.FORBIDDEN).build();
    }
    
    forumController.deleteTopicWatcher(watcher);

    return Response.noContent().build();
  }

  private fi.foyt.fni.rest.forum.model.ForumTopicWatcher createRestModel(ForumTopicWatcher topicWatcher) {
    return new fi.foyt.fni.rest.forum.model.ForumTopicWatcher(topicWatcher.getId(), topicWatcher.getUser().getId());
  }

  private fi.foyt.fni.rest.forum.model.ForumTopicWatcher[] createRestModel(ForumTopicWatcher... watchers) {
    List<fi.foyt.fni.rest.forum.model.ForumTopicWatcher> result = new ArrayList<>();
    
    for (ForumTopicWatcher watcher : watchers) {
      result.add(createRestModel(watcher));
    }
    
    return result.toArray(new fi.foyt.fni.rest.forum.model.ForumTopicWatcher[0]);
  }
}

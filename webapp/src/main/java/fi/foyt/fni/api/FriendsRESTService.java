package fi.foyt.fni.api;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.api.beans.CompactUserBean;
import fi.foyt.fni.api.beans.CompactUserFriendBean;
import fi.foyt.fni.api.beans.CompleteCommonFriendBean;
import fi.foyt.fni.api.events.FriendRemovedEvent;
import fi.foyt.fni.api.events.FriendRequestConfirmedEvent;
import fi.foyt.fni.api.events.FriendRequestEvent;
import fi.foyt.fni.persistence.dao.users.FriendConfirmKeyDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.dao.users.UserEmailDAO;
import fi.foyt.fni.persistence.dao.users.UserFriendDAO;
import fi.foyt.fni.persistence.model.users.CommonFriend;
import fi.foyt.fni.persistence.model.users.FriendConfirmKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserEmail;
import fi.foyt.fni.persistence.model.users.UserFriend;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.users.UserController;

@Path("/friends")
@RequestScoped
@Stateful
@Produces ("application/json")
public class FriendsRESTService extends RESTService {
	
	@Inject
	private UserController userController;
	
	@Inject
	private Event<FriendRequestEvent> friendRequestEvent;

	@Inject
	private Event<FriendRequestConfirmedEvent> friendRequestConfirmedEvent;
	
	@Inject
	private Event<FriendRemovedEvent> friendRemoved;

	@Inject
	private UserDAO userDAO;

	@Inject
	private UserEmailDAO userEmailDAO;
	
	@Inject
	private UserFriendDAO userFriendDAO;
	
	@Inject
	private FriendConfirmKeyDAO friendConfirmKeyDAO;
	
	@PUT
	@POST
	@Path ("/{USERID}/addFriend")
	public Response addFriend(
			@PathParam("USERID") String userIdParam,
		  @FormParam ("friendId") Long friendId,	
		  @FormParam ("friendEmail") String friendEmail,
		  @Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders) {
			
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		User user = null;
		User friend = null;
		
		if ("SELF".equals(userIdParam)) {
			user = loggedUser;
		} else {
  		return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "USERID")).build();
		}
		
		if (friendId != null) {
			friend = userDAO.findById(friendId);
		} else {
			if (friendEmail != null) {
				UserEmail friendUserEmail = userEmailDAO.findByEmail(friendEmail);
				if (friendUserEmail != null) {
					friend = friendUserEmail.getUser();
				}
			}
		}
		
		if (friend == null) {
			return Response.status(Status.NOT_FOUND).entity(ApiMessages.getText(browserLocale, "friends.addFriend.error.userNotFound")).build();
		}
		
		UserFriend userFriend = userFriendDAO.findByUserAndFriend(user, friend);
		if (userFriend != null) {
			// User has already asked for friendship
			if (userFriend.getConfirmed() == false) {
				// ..but the friendship is not verified, so we just ask for confirmation again
				
				FriendConfirmKey friendConfirmKey = friendConfirmKeyDAO.create(user, friend, UUID.randomUUID().toString());
				friendRequestEvent.fire(new FriendRequestEvent(uriInfo, friendConfirmKey.getUser(), friendConfirmKey.getFriend(), friendConfirmKey.getValue()));
			}
		} else {
			userFriend = userFriendDAO.findByUserAndFriend(friend, user);
			if (userFriend != null) {
				if (!userFriend.getConfirmed()) {
					// Friend has asked for friendship but user has not confirmed it, so we just confirm it 
					// instead of sending new confirmation
					
					userFriend = userFriendDAO.updateConfirmed(userFriend, Boolean.TRUE);
					friendRequestConfirmedEvent.fire(new FriendRequestConfirmedEvent(uriInfo, user, friend));

					List<FriendConfirmKey> friendConfirmKeys = friendConfirmKeyDAO.listByUserAndFriend(user, friend);
					for (FriendConfirmKey friendConfirmKey : friendConfirmKeys) {
						friendConfirmKeyDAO.delete(friendConfirmKey);
					}
					
					friendConfirmKeys = friendConfirmKeyDAO.listByUserAndFriend(friend, user);
					for (FriendConfirmKey friendConfirmKey : friendConfirmKeys) {
						friendConfirmKeyDAO.delete(friendConfirmKey);
					}
				}
			} else {
				userFriend = userFriendDAO.create(user, friend, Boolean.FALSE);
				FriendConfirmKey friendConfirmKey = friendConfirmKeyDAO.create(user, friend, UUID.randomUUID().toString());
				friendRequestEvent.fire(new FriendRequestEvent(uriInfo, friendConfirmKey.getUser(), friendConfirmKey.getFriend(), friendConfirmKey.getValue()));
			}
		}
		
		return Response.ok(new ApiResult<CompactUserFriendBean>(CompactUserFriendBean.fromEntity(userFriend))).build();
	}
	
	@DELETE
	@POST
	@Path ("/{USERID}/removeFriend/{FRIENDID}")
	public Response removeFriend(
			@PathParam("USERID") String userIdParam,
		  @PathParam ("FRIENDID") Long friendId,	
		  @Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		User user = null;
		User friend = null;
		
		if ("SELF".equals(userIdParam)) {
			user = loggedUser;
		} else {
  		return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "USERID")).build();
		}
		
		if (friendId != null) {
			friend = userDAO.findById(friendId);
		} else {
  		return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "FRIENDID")).build();
		}
		
		UserFriend userFriend1 = userFriendDAO.findByUserAndFriend(user, friend);
		UserFriend userFriend2 = userFriendDAO.findByUserAndFriend(friend, user);

		if (userFriend1 == null && userFriend2 == null) {
  		return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "USERID")).build();
		}
		
		if (userFriend1 != null)
			userFriendDAO.delete(userFriend1);
		if (userFriend2 != null)
		  userFriendDAO.delete(userFriend2);
			
		friendRemoved.fire(new FriendRemovedEvent(uriInfo, user, friend));
		
		return Response.ok().build();
	}
	
	/**
   * Lists friends
   */
	@GET
	@Path ("/{USERID}/listFriends")
	public Response createUser(
			@PathParam("USERID") String userIdParam,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		User user = null;
		
		if ("SELF".equals(userIdParam)) {
			user = loggedUser;
		} else {
  			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", userIdParam)).build();
		}

		List<User> friends = userController.listUserFriends(user);
		Map<String, List<CompactUserBean>> result = new HashMap<String, List<CompactUserBean>>();
		result.put("friends", CompactUserBean.fromEntities(friends));
		
		return Response.ok(new ApiResult<>(result)).build();
	}
	
	/**
   * Suggests new friends
   */
	@GET
	@Path ("/{USERID}/suggestFriends")
	public Response suggestFriends(
			@PathParam("USERID") String userIdParam,
			@QueryParam ("firstResult") Integer firstResult,
			@QueryParam ("maxResults") Integer maxResults,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);

		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		User user = null;
		
		if ("SELF".equals(userIdParam)) {
			user = loggedUser;
		} else {
  		return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "USERID")).build();
		}
		
		List<CommonFriend> commonFriends = userFriendDAO.listCommonFriendsByUserOrderByCommonFriendCount(user, firstResult, maxResults);
		
		List<CompleteCommonFriendBean> result = CompleteCommonFriendBean.fromEntities(commonFriends);
		
		return Response.ok(new ApiResult<>(result)).build();
	}

	@GET
	@Path ("/confirmFriend/{CONFIRMKEY}")
	public Response confirmFriend(
			@PathParam("CONFIRMKEY") String confirmKey,	
			@QueryParam("redirectUrl") String redirectUrl,
			@Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		
		FriendConfirmKey friendConfirmKey = friendConfirmKeyDAO.findByValue(confirmKey);
		if (friendConfirmKey == null) {
  		return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "CONFIRMKEY")).build();
		}
		
		UserFriend userFriend = userFriendDAO.findByUserAndFriend(friendConfirmKey.getUser(), friendConfirmKey.getFriend());
		if (userFriend == null) {
			userFriend = userFriendDAO.findByUserAndFriend(friendConfirmKey.getFriend(), friendConfirmKey.getUser());
		}
		
		userFriend = userFriendDAO.updateConfirmed(userFriend, Boolean.TRUE);
		friendRequestConfirmedEvent.fire(new FriendRequestConfirmedEvent(uriInfo, friendConfirmKey.getUser(), friendConfirmKey.getFriend()));

		friendConfirmKeyDAO.delete(friendConfirmKey);
		
		try {
			if (StringUtils.isBlank(redirectUrl)) {
				redirectUrl = getApplicationBaseUrl(uriInfo);
	   	}
      return Response.temporaryRedirect(new URI(redirectUrl)).build();
    } catch (URISyntaxException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    } catch (MalformedURLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ApiMessages.getText(browserLocale, "error.generic.configurationError")).build();
    }
	}
	
	
}

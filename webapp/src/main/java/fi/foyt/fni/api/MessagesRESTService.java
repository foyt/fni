package fi.foyt.fni.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fi.foyt.fni.api.beans.CompactMessageFolderBean;
import fi.foyt.fni.api.beans.CompactRecipientMessageBean;
import fi.foyt.fni.api.events.PrivateMessageSentEvent;
import fi.foyt.fni.messages.MessageController;
import fi.foyt.fni.messages.MessageController.MessageBean;
import fi.foyt.fni.persistence.dao.messages.MessageDAO;
import fi.foyt.fni.persistence.dao.messages.MessageFolderDAO;
import fi.foyt.fni.persistence.dao.messages.RecipientMessageDAO;
import fi.foyt.fni.persistence.dao.users.UserDAO;
import fi.foyt.fni.persistence.model.messages.Message;
import fi.foyt.fni.persistence.model.messages.MessageFolder;
import fi.foyt.fni.persistence.model.messages.RecipientMessage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;

@Path("/messages")
@RequestScoped
@Stateful
@Produces ("application/json")
public class MessagesRESTService extends RESTService {
	
	@Inject
	private MessageController messageController;
	
	@Inject
	private Event<PrivateMessageSentEvent> privateMessageSentEvent;

	@Inject
	private MessageDAO messageDAO;

	@Inject
	private MessageFolderDAO messageFolderDAO;

	@Inject
	private RecipientMessageDAO recipientMessageDAO;
	
	@Inject
	private UserDAO userDAO;
	
	@POST
	@PUT
	@Path ("/sendMessage") 
	public Response sendMessage(
			@FormParam ("threadId") String threadId,
			@FormParam ("subject") String subject,
			@FormParam ("content") String content,
			@FormParam ("recipients") String recipientsParam,
			@Context UriInfo uriInfo,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

		Date sent = new Date();
    String[] recipientStrs = recipientsParam.split(",");
    
    if (StringUtils.isBlank(threadId))
    	threadId = RandomStringUtils.randomAlphanumeric(10);
    
    List<User> recipients = new ArrayList<User>();
    for (String recipientStr : recipientStrs) {
    	Long recipientId = NumberUtils.createLong(recipientStr);
    	User recipient = userDAO.findById(recipientId);
    	recipients.add(recipient);
    }
    
    List<RecipientMessage> recipientMessages = messageController.sendMessage(loggedUser, recipients, threadId, sent, subject, content);
    for (RecipientMessage recipientMessage : recipientMessages) {
    	privateMessageSentEvent.fire(new PrivateMessageSentEvent(uriInfo, recipientMessage));
    }
    
    Map<String, List<CompactRecipientMessageBean>> result = new HashMap<String, List<CompactRecipientMessageBean>>();
    result.put("messages", CompactRecipientMessageBean.fromEntities(recipientMessages));
    return Response.ok(new ApiResult<>(result)).build();
	}
	
	@GET
	@Path ("/listFolders")
	public Response listFolders(@Context HttpHeaders httpHeaders) {
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		List<MessageFolder> folders = messageFolderDAO.listByOwner(loggedUser);
		
		Map<String, List<CompactMessageFolderBean>> result = new HashMap<String, List<CompactMessageFolderBean>>();
		result.put("folders", CompactMessageFolderBean.fromEntities(folders));
		
		return Response.ok(new ApiResult<>(result)).build();
	}

	@GET
	@Path ("/{MESSAGEID}")
	public Response message(
			@PathParam ("MESSAGEID") Long messageId,
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Message message = messageDAO.findById(messageId);

		if (!message.getSender().getId().equals(loggedUser.getId())) {
			if (!isMessageRecipient(message, loggedUser)) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
		}

		List<MessageBean> messages = messageController.recipientMessagesToMessageBeans(recipientMessageDAO.listByMessage(message), loggedUser, true);
		MessageBean messageBean = messages.size() == 0 ? null : messages.get(0);

		return Response.ok(new ApiResult<>(messageBean)).build();
	}

	@GET
	@Path ("/listMessages/{FOLDERID}")
	public Response listMessages(
			@PathParam ("FOLDERID") String folderIdParam,
			@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}

		List<MessageBean> messages = null;
				
		if ("INBOX".equals(folderIdParam)) {
			messages = messageController.recipientMessagesToMessageBeans(recipientMessageDAO.listByRootFolderAndRecipientAndRemoved(loggedUser, Boolean.FALSE), loggedUser, false);
		} else if ("OUTBOX".equals(folderIdParam)) {
			messages = messageController.recipientMessagesToMessageBeans(recipientMessageDAO.listBySender(loggedUser), loggedUser, false);
		} else if ("STARRED".equals(folderIdParam)) {
			messages = messageController.recipientMessagesToMessageBeans(recipientMessageDAO.listByRecipientAndStarred(loggedUser, Boolean.TRUE), loggedUser, false);
		} else if ("TRASH".equals(folderIdParam)) {
			messages = messageController.recipientMessagesToMessageBeans(recipientMessageDAO.listByRecipientAndRemoved(loggedUser, Boolean.TRUE), loggedUser, false);
		} else {
			Long folderId = NumberUtils.createLong(folderIdParam);
			MessageFolder messageFolder = messageFolderDAO.findById(folderId);
			if (!messageFolder.getOwner().getId().equals(loggedUser.getId())) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
			
			messages = messageController.recipientMessagesToMessageBeans(recipientMessageDAO.listByFolder(messageFolder), loggedUser, false);
		}
		
		Collections.sort(messages, new Comparator<MessageBean>() {
			@Override
			public int compare(MessageBean o1, MessageBean o2) {
			  return o2.getSent().compareTo(o1.getSent());
			}
		});

		Map<String, List<MessageBean>> result = new HashMap<String, List<MessageBean>>();
		result.put("messages", messages);

		return Response.ok(new ApiResult<>(result)).build();
	}

	@PUT
	@POST
	@Path ("/{MESSAGEID}/markRead")
	public Response markRead(
			@PathParam ("MESSAGEID") Long messageId,
			@Context HttpHeaders httpHeaders) {
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Message message = messageDAO.findById(messageId);
		
		if (!isMessageRecipient(message, loggedUser)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
				
		RecipientMessage recipientMessage = recipientMessageDAO.findByMessageAndRecipient(message, loggedUser);
		if (recipientMessage == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "MESSAGEID")).build();
		}
		
		recipientMessageDAO.updateRead(recipientMessage, Boolean.TRUE);
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("recipientMessage", CompactRecipientMessageBean.fromEntity(recipientMessage));
		result.put("unreadMessages", recipientMessageDAO.countByRecipientAndReadAndRemoved(loggedUser, Boolean.FALSE, Boolean.FALSE));
		
		return Response.ok(new ApiResult<>(result)).build();
	}

	@PUT
	@POST
	@Path ("/{MESSAGEID}/star")
	public Response star(
			@PathParam ("MESSAGEID") Long messageId,
			@Context HttpHeaders httpHeaders) {

		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Message message = messageDAO.findById(messageId);
		
		if (!isMessageRecipient(message, loggedUser)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
				
		RecipientMessage recipientMessage = recipientMessageDAO.findByMessageAndRecipient(message, loggedUser);
		if (recipientMessage == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "MESSAGEID")).build();
		}
		
		recipientMessageDAO.updateStarred(recipientMessage, Boolean.TRUE);
		
		return Response.ok(new ApiResult<>(CompactRecipientMessageBean.fromEntity(recipientMessage))).build();
	}

	@PUT
	@POST
	@Path ("/{MESSAGEID}/unstar")
  public Response unstar(
  		@PathParam ("MESSAGEID") Long messageId,
  		@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Message message = messageDAO.findById(messageId);
		
		if (!isMessageRecipient(message, loggedUser)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
				
		RecipientMessage recipientMessage = recipientMessageDAO.findByMessageAndRecipient(message, loggedUser);
		if (recipientMessage == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "MESSAGEID")).build();
		}
		
		recipientMessageDAO.updateStarred(recipientMessage, Boolean.FALSE);
		
		return Response.ok(new ApiResult<>(CompactRecipientMessageBean.fromEntity(recipientMessage))).build();
	}

	@POST
	@Path ("/{MESSAGEID}/moveMessage")
	public Response moveMessage(
			@PathParam ("MESSAGEID") Long messageId,
			@FormParam ("folderId") String folderIdParam, 
			@Context HttpHeaders httpHeaders) {
		
		Locale browserLocale = getBrowserLocale(httpHeaders);
		User loggedUser = getLoggedUser(httpHeaders);
		
		if (!hasRole(loggedUser, UserRole.USER)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		Message message = messageDAO.findById(messageId);
		
		if (!isMessageRecipient(message, loggedUser)) {
			return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
		}
		
		RecipientMessage recipientMessage = recipientMessageDAO.findByMessageAndRecipient(message, loggedUser);
		if (recipientMessage == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity(ApiMessages.getText(browserLocale, "error.generic.invalidParameter", "MESSAGEID")).build();
		}

		if ("INBOX".equals(folderIdParam)) {
			recipientMessageDAO.updateFolder(recipientMessage, null);
			recipientMessageDAO.updateRemoved(recipientMessage, Boolean.FALSE); 
		} else if ("TRASH".equals(folderIdParam)) {
			recipientMessageDAO.updateRemoved(recipientMessage, Boolean.TRUE); 
		} else {
			Long folderId = NumberUtils.createLong(folderIdParam);
			MessageFolder messageFolder = messageFolderDAO.findById(folderId);
			
			if (!messageFolder.getOwner().getId().equals(loggedUser.getId())) {
				return Response.status(Response.Status.FORBIDDEN).entity(ApiMessages.getText(browserLocale, "error.generic.permissionDenied")).build();
			}
			
			recipientMessageDAO.updateRemoved(recipientMessage, Boolean.FALSE); 
			recipientMessageDAO.updateFolder(recipientMessage, messageFolder);
		}
		
		return Response.ok(new ApiResult<>(CompactRecipientMessageBean.fromEntity(recipientMessage))).build();
	}
	
	private boolean isMessageRecipient(Message message, User user) {
		return recipientMessageDAO.findByMessageAndRecipient(message, user) != null;
  }
}

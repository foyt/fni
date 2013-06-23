package fi.foyt.fni.api.events;

import javax.ws.rs.core.UriInfo;

import fi.foyt.fni.persistence.model.messages.RecipientMessage;

public class PrivateMessageSentEvent extends ApiEvent {
	
	public PrivateMessageSentEvent(UriInfo uriInfo, RecipientMessage recipientMessage) {
		super(uriInfo);
		this.recipientMessage = recipientMessage;
	}
	
	public RecipientMessage getRecipientMessage() {
		return recipientMessage;
	}

	private RecipientMessage recipientMessage;
}

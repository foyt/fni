package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.List;

import fi.foyt.fni.persistence.model.messages.RecipientMessage;

public class CompactRecipientMessageBean {

	public CompactRecipientMessageBean(Long id, Long messageId, Boolean read, Boolean removed, Long recipientId, Boolean starred, Long folderId) {
		this.id = id;
		this.messageId = messageId;
		this.read = read;
		this.removed = removed;
		this.recipientId = recipientId;
		this.starred = starred;
		this.folderId = folderId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean getRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public Boolean getStarred() {
		return starred;
	}

	public void setStarred(Boolean starred) {
		this.starred = starred;
	}

	public Long getFolderId() {
		return folderId;
	}

	public void setFolderId(Long folderId) {
		this.folderId = folderId;
	}
	
	public static CompactRecipientMessageBean fromEntity(RecipientMessage entity) {
		if (entity == null)
			return null;
		
		Long folderId = entity.getFolder() != null ? entity.getFolder().getId() : null;
		return new CompactRecipientMessageBean(entity.getId(), entity.getMessage().getId(), entity.getRead(), entity.getRemoved(),
		    entity.getRecipient().getId(), entity.getStarred(), folderId);
	}

	public static List<CompactRecipientMessageBean> fromEntities(List<RecipientMessage> entities) {
		List<CompactRecipientMessageBean> beans = new ArrayList<CompactRecipientMessageBean>(entities.size());

		for (RecipientMessage entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	private Long id;

	private Long messageId;

	private Boolean read;

	private Boolean removed;

	private Long recipientId;

	private Boolean starred;

	private Long folderId;
}

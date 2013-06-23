package fi.foyt.fni.persistence.model.messages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class RecipientMessage {
	
	public Long getId() {
	  return id;
  }
	
	public Message getMessage() {
	  return message;
  }
	
	public void setMessage(Message message) {
	  this.message = message;
  }

	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
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

	public Boolean getStarred() {
		return starred;
	}

	public void setStarred(Boolean starred) {
		this.starred = starred;
	}

	public MessageFolder getFolder() {
		return folder;
	}

	public void setFolder(MessageFolder folder) {
		this.folder = folder;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Message message;

	@Column(name = "read_", nullable = false, columnDefinition = "BIT")
	private Boolean read;

	@Column(nullable = false, columnDefinition = "BIT")
	private Boolean removed;

	@ManyToOne
	private User recipient;

	@Column(nullable = false, columnDefinition = "BIT")
	private Boolean starred;

	@ManyToOne
	private MessageFolder folder;
}

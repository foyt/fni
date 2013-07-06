package fi.foyt.fni.rest.entities.messages;

import fi.foyt.fni.rest.entities.users.User;

public class MessageFolder {

	public Long getId() {
	  return id;
  }
	
	public String getName() {
	  return name;
  }
	
	public void setName(String name) {
	  this.name = name;
  }
	
	public User getSender() {
		return sender;
	}
	
	public void setSender(User sender) {
		this.sender = sender;
	}
	
	public Boolean getRead() {
		return read;
	}
	
	public void setRead(Boolean read) {
		this.read = read;
	}
	
	public User getRecipient() {
		return recipient;
	}
	
	public void setRecipient(User recipient) {
		this.recipient = recipient;
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

	private Long id;
  
  private String name;
    
  private User sender;
  
  private Boolean read;

	private User recipient;

	private Boolean starred;

	private MessageFolder folder;
}

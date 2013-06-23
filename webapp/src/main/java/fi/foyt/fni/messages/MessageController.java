package fi.foyt.fni.messages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.api.beans.CompactUserBean;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.messages.MessageDAO;
import fi.foyt.fni.persistence.dao.messages.RecipientMessageDAO;
import fi.foyt.fni.persistence.model.messages.Message;
import fi.foyt.fni.persistence.model.messages.RecipientMessage;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@Named
@RequestScoped
@Stateful
public class MessageController {
  
  @Inject
  private SessionController sessionController;

	@Inject
	@DAO
	private MessageDAO messageDAO;
	
  @Inject
  @DAO
  private RecipientMessageDAO recipientMessageDAO;
  
  public boolean getNewMessages() {
    return getNewMessages(sessionController.getLoggedUser());
  }
  
  public boolean getNewMessages(User user) {
    return getNewMessageCount(user) > 0;
  }

  public int getNewMessageCount() {
    return getNewMessageCount(sessionController.getLoggedUser());
  }
  
  public int getNewMessageCount(User user) {
    return recipientMessageDAO.countByRecipientAndReadAndRemoved(user, Boolean.FALSE, Boolean.FALSE).intValue();
  }
  
  public RecipientMessage sendMessage(User sender, User recipient, String subject, String content) {
		return sendMessage(sender, Arrays.asList(recipient), null, null, subject, content).get(0);
	}
	
	public List<RecipientMessage> sendMessage(User sender, List<User> recipients, String threadId, Date sent, String subject, String content) {
    if (sent == null)
    	sent = new Date();
		
		if (StringUtils.isBlank(threadId))
    	threadId = RandomStringUtils.randomAlphanumeric(10);
    
    List<RecipientMessage> recipientMessages = new ArrayList<RecipientMessage>();
    
    Message message = messageDAO.create(threadId, sender, subject, content, sent);
    
    for (User recipient : recipients) {
      recipientMessages.add(recipientMessageDAO.create(message, recipient, null, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
    }
    
    return recipientMessages;
	}

	public List<MessageBean> recipientMessagesToMessageBeans(List<RecipientMessage> recipientMessages, User loggedUser, boolean includeContents) {
		List<MessageBean> result = new ArrayList<MessageBean>();
		for (RecipientMessage recipientMessage : recipientMessages) {
			Message message = recipientMessage.getMessage();
			MessageBean messageBean = null;
			
			for (MessageBean bean : result) {
				if (bean.getId().equals(message.getId())) {
					messageBean = bean;
					break;
				}
			}
			
			if (messageBean == null) {
				messageBean = new MessageBean(message.getId(), message.getSent(), message.getThreadId(), message.getSubject(), includeContents ? message.getContent() : null, CompactUserBean.fromEntity(message.getSender()), recipientMessage.getStarred());
			}
			
			messageBean.addRecipient(CompactUserBean.fromEntity(recipientMessage.getRecipient()));

			if (loggedUser != null && loggedUser.getId().equals(recipientMessage.getRecipient().getId())) {
				messageBean.setRead(recipientMessage.getRead());
			}
			
			result.add(messageBean);
		}
		
		return result;
	}

	public class MessageBean {

		public MessageBean(Long id, Date sent, String threadId, String subject, String content, CompactUserBean sender, Boolean starred) {
			super();
			this.id = id;
			this.sent = sent;
			this.threadId = threadId;
			this.subject = subject;
			this.sender = sender;
			this.starred = starred;
			this.content = content;
		}

		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
	    this.id = id;
    }
		
		public Boolean getRead() {
	    return read;
    }
		
		public void setRead(Boolean read) {
	    this.read = read;
    }

		public Date getSent() {
			return sent;
		}
		
		public void setSent(Date sent) {
	    this.sent = sent;
    }

		public String getThreadId() {
			return threadId;
		}
		
		public void setThreadId(String threadId) {
	    this.threadId = threadId;
    }

		public String getSubject() {
			return subject;
		}
		
		public void setSubject(String subject) {
	    this.subject = subject;
    }
		
		public String getContent() {
	    return content;
    }
		
		public void setContent(String content) {
	    this.content = content;
    }

		public CompactUserBean getSender() {
	    return sender;
    }
		
		public void setSender(CompactUserBean sender) {
	    this.sender = sender;
    }

		public Boolean getStarred() {
			return starred;
		}
		
		public void setStarred(Boolean starred) {
	    this.starred = starred;
    }

		public List<CompactUserBean> getRecipients() {
			return recipients;
		}
		
		public void setRecipients(List<CompactUserBean> recipients) {
	    this.recipients = recipients;
    }
		
		public void addRecipient(CompactUserBean recipient) {
	    this.recipients.add(recipient);
    }

		private Long id;
		private Boolean read = Boolean.FALSE;
		private Date sent;
		private String threadId;
		private String subject;
		private String content;
		private CompactUserBean sender;
		private Boolean starred;
		private List<CompactUserBean> recipients = new ArrayList<CompactUserBean>();
	}
}

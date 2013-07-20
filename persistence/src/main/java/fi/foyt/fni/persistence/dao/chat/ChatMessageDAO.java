package fi.foyt.fni.persistence.dao.chat;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.chat.ChatMessage;
import fi.foyt.fni.persistence.model.chat.ChatMessage_;
import fi.foyt.fni.persistence.model.chat.XmppUser;

@DAO
public class ChatMessageDAO extends GenericDAO<ChatMessage> {
	
  private static final long serialVersionUID = 1L;

	public ChatMessage create(XmppUser from, XmppUser to, String body, String subject, Date sent, Boolean received) {
    ChatMessage chatMessage = new ChatMessage();
    
    chatMessage.setBody(body);
    chatMessage.setFrom(from);
    chatMessage.setSent(sent);
    chatMessage.setSubject(subject);
    chatMessage.setTo(to);
    chatMessage.setReceived(received);
    
    getEntityManager().persist(chatMessage);
    
    return chatMessage;
  }

  public List<ChatMessage> listByReceivedAndTo(Boolean received, XmppUser to) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ChatMessage> criteria = criteriaBuilder.createQuery(ChatMessage.class);
    Root<ChatMessage> root = criteria.from(ChatMessage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(ChatMessage_.received), received),
        criteriaBuilder.equal(root.get(ChatMessage_.to), to)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public List<ChatMessage> listByReceivedToAndFrom(Boolean received, XmppUser to, XmppUser from) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ChatMessage> criteria = criteriaBuilder.createQuery(ChatMessage.class);
    Root<ChatMessage> root = criteria.from(ChatMessage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(ChatMessage_.received), received),
        criteriaBuilder.equal(root.get(ChatMessage_.to), to),
        criteriaBuilder.equal(root.get(ChatMessage_.from), from)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByTo(XmppUser to) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ChatMessage> root = criteria.from(ChatMessage.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(ChatMessage_.to), to));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Long countByToAndFrom(XmppUser to, XmppUser from) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ChatMessage> root = criteria.from(ChatMessage.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(ChatMessage_.to), to),
        criteriaBuilder.equal(root.get(ChatMessage_.from), from)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public ChatMessage updateReceived(ChatMessage chatMessage, Boolean received) {
    chatMessage.setReceived(received);
    getEntityManager().persist(chatMessage);
    return chatMessage;
  }
}
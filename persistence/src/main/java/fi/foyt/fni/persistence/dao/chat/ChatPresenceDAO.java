package fi.foyt.fni.persistence.dao.chat;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.chat.ChatPresence;
import fi.foyt.fni.persistence.model.chat.ChatPresence_;
import fi.foyt.fni.persistence.model.chat.XmppUser;

@RequestScoped
@DAO
public class ChatPresenceDAO extends GenericDAO<ChatPresence> {
	
	ChatPresenceDAO() {
  }

  public ChatPresence create(XmppUser from, XmppUser to, String statusMessage, String type, String mode, Date sent) {
    ChatPresence chatPresence = new ChatPresence();
    
    chatPresence.setStatusMessage(statusMessage);
    chatPresence.setFrom(from);
    chatPresence.setTo(to);
    chatPresence.setSent(sent);
    chatPresence.setType(type);
    chatPresence.setMode(mode);
    
    getEntityManager().persist(chatPresence);
    
    return chatPresence;
  }

  public List<ChatPresence> listByTo(XmppUser to) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ChatPresence> criteria = criteriaBuilder.createQuery(ChatPresence.class);
    Root<ChatPresence> root = criteria.from(ChatPresence.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ChatPresence_.to), to));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByFrom(XmppUser from) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ChatPresence> root = criteria.from(ChatPresence.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(ChatPresence_.from), from));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}
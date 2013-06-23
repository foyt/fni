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
import fi.foyt.fni.persistence.model.chat.MultiUserChatPresence;
import fi.foyt.fni.persistence.model.chat.MultiUserChatPresence_;
import fi.foyt.fni.persistence.model.chat.XmppUser;

@RequestScoped
@DAO
public class MultiUserChatPresenceDAO extends GenericDAO<MultiUserChatPresence> {
	
	MultiUserChatPresenceDAO() {
  }

  public MultiUserChatPresence create(XmppUser from, XmppUser to, String roomJid, String statusMessage, String type, String mode, Date sent) {
    MultiUserChatPresence multiUserChatPresence = new MultiUserChatPresence();
    
    multiUserChatPresence.setTo(to);
    multiUserChatPresence.setFrom(from);
    multiUserChatPresence.setSent(sent);
    multiUserChatPresence.setMode(mode);
    multiUserChatPresence.setType(type);
    multiUserChatPresence.setStatusMessage(statusMessage);
    multiUserChatPresence.setRoomJid(roomJid);
    
    getEntityManager().persist(multiUserChatPresence);
    
    return multiUserChatPresence;
  }

  public List<MultiUserChatPresence> listByRoomJid(String roomJid) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MultiUserChatPresence> criteria = criteriaBuilder.createQuery(MultiUserChatPresence.class);
    Root<MultiUserChatPresence> root = criteria.from(MultiUserChatPresence.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(MultiUserChatPresence_.roomJid), roomJid));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByTo(String roomJid) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<MultiUserChatPresence> root = criteria.from(MultiUserChatPresence.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(MultiUserChatPresence_.roomJid), roomJid));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}
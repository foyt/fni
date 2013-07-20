package fi.foyt.fni.persistence.dao.chat;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.chat.MultiUserChatMessage;
import fi.foyt.fni.persistence.model.chat.MultiUserChatMessage_;
import fi.foyt.fni.persistence.model.chat.XmppUser;

@DAO
public class MultiUserChatMessageDAO extends GenericDAO<MultiUserChatMessage> {

	private static final long serialVersionUID = 1L;

	MultiUserChatMessageDAO() {
  }
	
  public MultiUserChatMessage create(String roomJid, XmppUser from, String body, String subject, Date sent) {
    MultiUserChatMessage multiUserChatMessage = new MultiUserChatMessage();
    
    multiUserChatMessage.setBody(body);
    multiUserChatMessage.setFrom(from);
    multiUserChatMessage.setSent(sent);
    multiUserChatMessage.setSubject(subject);
    multiUserChatMessage.setRoomJid(roomJid);
    
    getEntityManager().persist(multiUserChatMessage);
    
    return multiUserChatMessage;
  }

  public List<MultiUserChatMessage> listByRoomJid(String roomJid) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MultiUserChatMessage> criteria = criteriaBuilder.createQuery(MultiUserChatMessage.class);
    Root<MultiUserChatMessage> root = criteria.from(MultiUserChatMessage.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(MultiUserChatMessage_.roomJid), roomJid));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByTo(String roomJid) {
    EntityManager entityManager = getEntityManager();
    
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<MultiUserChatMessage> root = criteria.from(MultiUserChatMessage.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(MultiUserChatMessage_.roomJid), roomJid));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}
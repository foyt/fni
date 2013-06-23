package fi.foyt.fni.persistence.dao.messages;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.messages.Message;
import fi.foyt.fni.persistence.model.messages.Message_;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class MessageDAO extends GenericDAO<Message> {

	public Message create(String threadId, User sender, String subject, String content, Date sent) {
		Message message = new Message();
		message.setContent(content);
		message.setSender(sender);
		message.setSent(sent);
		message.setSubject(subject);
		message.setThreadId(threadId);
		
		getEntityManager().persist(message);
		
		return message;
	}
  
  public List<Message> listByThread(String threadId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Message> criteria = criteriaBuilder.createQuery(Message.class);
    Root<Message> root = criteria.from(Message.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Message_.threadId), threadId)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
}

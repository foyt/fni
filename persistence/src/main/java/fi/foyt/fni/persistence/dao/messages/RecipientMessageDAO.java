package fi.foyt.fni.persistence.dao.messages;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.messages.Message;
import fi.foyt.fni.persistence.model.messages.Message_;
import fi.foyt.fni.persistence.model.messages.RecipientMessage;
import fi.foyt.fni.persistence.model.messages.MessageFolder;
import fi.foyt.fni.persistence.model.messages.RecipientMessage_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class RecipientMessageDAO extends GenericDAO<RecipientMessage> {
	
	private static final long serialVersionUID = 1L;

	public RecipientMessage create(Message message, User recipient, MessageFolder folder, Boolean read, Boolean starred, Boolean removed) {
		RecipientMessage recipientMessage = new RecipientMessage();
		recipientMessage.setFolder(folder);
		recipientMessage.setMessage(message);
		recipientMessage.setRead(read);
		recipientMessage.setRecipient(recipient);
		recipientMessage.setStarred(starred);
		recipientMessage.setRemoved(removed);
		
		getEntityManager().persist(recipientMessage);
		
		return recipientMessage;
	}

	public RecipientMessage findByMessageAndRecipient(Message message, User recipient) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
        criteriaBuilder.equal(root.get(RecipientMessage_.recipient), recipient),
        criteriaBuilder.equal(root.get(RecipientMessage_.message), message)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

	public List<RecipientMessage> listByRecipient(User recipient) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(RecipientMessage_.recipient), recipient)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<RecipientMessage> listBySender(User sender) {
  	return listBySender(sender, null, null);
  }
  
  public List<RecipientMessage> listBySender(User sender, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    Join<RecipientMessage, Message> messageJoin = root.join(RecipientMessage_.message);
    
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(messageJoin.get(Message_.sender), sender)
    );
    
    TypedQuery<RecipientMessage> query = entityManager.createQuery(criteria);
    
    if (firstResult != null)
      query.setFirstResult(firstResult);
    
    if (maxResults != null)
      query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public List<RecipientMessage> listByFolder(MessageFolder folder) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);

    criteria.where(
      criteriaBuilder.equal(root.get(RecipientMessage_.folder), folder)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<RecipientMessage> listByRecipientAndStarred(User recipient, Boolean starred) {
    return listByRecipientAndStarred(recipient, starred, null, null);
  }
  
  public List<RecipientMessage> listByRecipientAndStarred(User recipient, Boolean starred, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
        criteriaBuilder.equal(root.get(RecipientMessage_.starred), starred),
        criteriaBuilder.equal(root.get(RecipientMessage_.recipient), recipient)
      )
    );
    
    TypedQuery<RecipientMessage> query = entityManager.createQuery(criteria);
    
    if (firstResult != null)
      query.setFirstResult(firstResult);
    
    if (maxResults != null)
      query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

	public List<RecipientMessage> listByRootFolderAndRecipientAndRemoved(User recipient, Boolean removed) {
	  return listByRootFolderAndRecipientAndRemoved(recipient, removed, null, null);
	}
		
	public List<RecipientMessage> listByRootFolderAndRecipientAndRemoved(User recipient, Boolean removed, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
        criteriaBuilder.isNull(root.get(RecipientMessage_.folder)),
        criteriaBuilder.equal(root.get(RecipientMessage_.recipient), recipient),
        criteriaBuilder.equal(root.get(RecipientMessage_.removed), removed)
      )
    );
    
    TypedQuery<RecipientMessage> query = entityManager.createQuery(criteria);
    
    if (firstResult != null)
      query.setFirstResult(firstResult);
    
    if (maxResults != null)
      query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public RecipientMessage updateFolder(RecipientMessage recipientMessage, MessageFolder folder) {
  	recipientMessage.setFolder(folder);
  	getEntityManager().persist(recipientMessage);
  	return recipientMessage;
  }

	public List<RecipientMessage> listByRecipientAndRemoved(User recipient, Boolean removed) {
		return listByRecipientAndRemoved(recipient, removed, null, null);
	}
	
	public List<RecipientMessage> listByRecipientAndRemoved(User recipient, Boolean removed, Integer firstResult, Integer maxResults) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
        criteriaBuilder.equal(root.get(RecipientMessage_.recipient), recipient),
        criteriaBuilder.equal(root.get(RecipientMessage_.removed), removed)
      )
    );
    
    TypedQuery<RecipientMessage> query = entityManager.createQuery(criteria);
    
    if (firstResult != null)
      query.setFirstResult(firstResult);
    
    if (maxResults != null)
      query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

	public List<RecipientMessage> listByMessage(Message message) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<RecipientMessage> criteria = criteriaBuilder.createQuery(RecipientMessage.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(RecipientMessage_.message), message)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public Long countByRecipientAndReadAndRemoved(User recipient, Boolean read, Boolean removed) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<RecipientMessage> root = criteria.from(RecipientMessage.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
    	criteriaBuilder.and(
        criteriaBuilder.equal(root.get(RecipientMessage_.recipient), recipient),
        criteriaBuilder.equal(root.get(RecipientMessage_.removed), removed),
        criteriaBuilder.equal(root.get(RecipientMessage_.read), read)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
	
	public RecipientMessage updateRead(RecipientMessage recipientMessage, Boolean read) {
		recipientMessage.setRead(read);
		getEntityManager().persist(recipientMessage);
		return recipientMessage;
	}
	
	public RecipientMessage updateStarred(RecipientMessage recipientMessage, Boolean starred) {
		recipientMessage.setStarred(starred);
		getEntityManager().persist(recipientMessage);
		return recipientMessage;
	}
	
	public RecipientMessage updateRemoved(RecipientMessage recipientMessage, Boolean removed) {
		recipientMessage.setRemoved(removed);
		getEntityManager().persist(recipientMessage);
		return recipientMessage;
	}
}

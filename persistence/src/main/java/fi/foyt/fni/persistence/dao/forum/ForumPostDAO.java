package fi.foyt.fni.persistence.dao.forum;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.forum.ForumPost_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.forum.ForumTopic_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ForumPostDAO extends GenericDAO<ForumPost> {

	private static final long serialVersionUID = 1L;

	public ForumPost create(ForumTopic topic, User author, Date created, Date modified, String content, Long views) {
    EntityManager entityManager = getEntityManager();
    ForumPost forumPost = new ForumPost();
    forumPost.setAuthor(author);
    forumPost.setCreated(created);
    forumPost.setTopic(topic);
    forumPost.setModified(modified);
    forumPost.setContent(content);
    forumPost.setViews(views);
    
    entityManager.persist(forumPost);
 
    return forumPost;
  }

  public List<ForumPost> listByTopic(ForumTopic topic) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumPost> criteria = criteriaBuilder.createQuery(ForumPost.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumPost_.topic), topic));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<ForumPost> listByTopic(ForumTopic topic, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumPost> criteria = criteriaBuilder.createQuery(ForumPost.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumPost_.topic), topic));
    
    TypedQuery<ForumPost> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);

    return query.getResultList();
  }

  public List<ForumPost> listByTopicSortByCreated(ForumTopic topic, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumPost> criteria = criteriaBuilder.createQuery(ForumPost.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumPost_.topic), topic));
    criteria.orderBy(criteriaBuilder.desc(root.get(ForumPost_.created)));
    TypedQuery<ForumPost> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public List<ForumPost> listByForumSortByCreated(Forum forum, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumPost> criteria = criteriaBuilder.createQuery(ForumPost.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    Join<ForumPost, ForumTopic> topicJoin = root.join(ForumPost_.topic);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(ForumPost_.created)));
    criteria.where(
  		criteriaBuilder.equal(topicJoin.join(ForumTopic_.forum), forum)
    );
    
    TypedQuery<ForumPost> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

	public List<ForumPost> listByAuthorSortByCreated(User author, int firstResult, int maxResults) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumPost> criteria = criteriaBuilder.createQuery(ForumPost.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(ForumPost_.created)));
    criteria.where(
  		criteriaBuilder.equal(root.get(ForumPost_.author), author)
    );
    
    TypedQuery<ForumPost> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
	}
	
	public List<ForumTopic> listTopicsSortByCreated(int firstResult, int maxResults) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopic> criteria = criteriaBuilder.createQuery(ForumTopic.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(root.get(ForumPost_.topic));
    criteria.orderBy(criteriaBuilder.desc(criteriaBuilder.greatest(root.get(ForumPost_.created))));
    criteria.groupBy(root.get(ForumPost_.topic));
    
    TypedQuery<ForumTopic> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
	}

  
  public List<ForumTopic> listTopicsByForumSortByCreated(Forum forum, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopic> criteria = criteriaBuilder.createQuery(ForumTopic.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    Join<ForumPost, ForumTopic> topicJoin = root.join(ForumPost_.topic);
    criteria.select(root.get(ForumPost_.topic)).distinct(true);
    criteria.orderBy(criteriaBuilder.desc(root.get(ForumPost_.created)));
    criteria.where(
  		criteriaBuilder.equal(topicJoin.get(ForumTopic_.forum), forum)
    );
    
    TypedQuery<ForumTopic> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public Long countByTopic(ForumTopic topic) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(ForumPost_.topic), topic));

    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Long countByAuthor(User author) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(ForumPost_.author), author));

    return entityManager.createQuery(criteria).getSingleResult();
  }

	public Long countByTopicAndAuthor(ForumTopic topic, User author) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
  		criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(ForumPost_.author), author),
    		criteriaBuilder.equal(root.get(ForumPost_.topic), topic)
    	)
    );

    return entityManager.createQuery(criteria).getSingleResult();
	}
  
  public Long countByForum(Forum forum) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ForumPost> root = criteria.from(ForumPost.class);
    Join<ForumPost, ForumTopic> topicJoin = root.join(ForumPost_.topic);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
  		criteriaBuilder.equal(topicJoin.join(ForumTopic_.forum), forum)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public ForumPost updateViews(ForumPost forumPost, Long views) {
    EntityManager entityManager = getEntityManager();

    forumPost.setViews(views);
    
    entityManager.persist(forumPost);
    
    return forumPost;
  }

  public ForumPost updateContent(ForumPost forumPost, String content) {
    forumPost.setContent(content);
    return persist(forumPost);
  }

  public ForumPost updateModified(ForumPost forumPost, Date modified) {
    forumPost.setModified(modified);
    return persist(forumPost);
  }

}

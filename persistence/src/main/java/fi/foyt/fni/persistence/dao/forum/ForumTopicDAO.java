package fi.foyt.fni.persistence.dao.forum;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.forum.ForumTopic_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ForumTopicDAO extends GenericDAO<ForumTopic> {

	private static final long serialVersionUID = 1L;

	public ForumTopic create(Forum forum, User author, Date created, Date modified, String urlName, String subject, Long views) {
    EntityManager entityManager = getEntityManager();

    ForumTopic forumTopic = new ForumTopic();
    forumTopic.setAuthor(author);
    forumTopic.setCreated(created);
    forumTopic.setForum(forum);
    forumTopic.setModified(modified);
    forumTopic.setSubject(subject);
    forumTopic.setUrlName(urlName);
    forumTopic.setViews(views);

    entityManager.persist(forumTopic);
    return forumTopic;
  }

  public ForumTopic findByForumAndUrlName(Forum forum, String urlName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopic> criteria = criteriaBuilder.createQuery(ForumTopic.class);
    Root<ForumTopic> root = criteria.from(ForumTopic.class);
    criteria.select(root);
    
    
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(ForumTopic_.urlName), urlName),
        criteriaBuilder.equal(root.get(ForumTopic_.forum), forum)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<ForumTopic> listByForum(Forum forum) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopic> criteria = criteriaBuilder.createQuery(ForumTopic.class);
    Root<ForumTopic> root = criteria.from(ForumTopic.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopic_.forum), forum));

    return entityManager.createQuery(criteria).getResultList();
  }

  public ForumTopic updateViews(ForumTopic forumTopic, Long views) {
    EntityManager entityManager = getEntityManager();

    forumTopic.setViews(views);
    
    entityManager.persist(forumTopic);
    
    return forumTopic;
  }

}

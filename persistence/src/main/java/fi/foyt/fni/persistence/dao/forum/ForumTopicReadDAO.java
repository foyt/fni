package fi.foyt.fni.persistence.dao.forum;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.forum.ForumTopicRead;
import fi.foyt.fni.persistence.model.forum.ForumTopicRead_;
import fi.foyt.fni.persistence.model.users.User;

public class ForumTopicReadDAO extends GenericDAO<ForumTopicRead> {

	private static final long serialVersionUID = 1L;

	public ForumTopicRead create(ForumTopic topic, User user, Date time) {
	  ForumTopicRead forumTopicRead = new ForumTopicRead();
	  forumTopicRead.setTopic(topic);
	  forumTopicRead.setUser(user);
	  forumTopicRead.setTime(time);
	  return persist(forumTopicRead);
	}

  public ForumTopicRead findByUserAndForumTopic(User user, ForumTopic forumTopic) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicRead> criteria = criteriaBuilder.createQuery(ForumTopicRead.class);
    Root<ForumTopicRead> root = criteria.from(ForumTopicRead.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(ForumTopicRead_.user), user),
        criteriaBuilder.equal(root.get(ForumTopicRead_.topic), forumTopic)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<ForumTopicRead> listByForumTopic(ForumTopic forumTopic) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicRead> criteria = criteriaBuilder.createQuery(ForumTopicRead.class);
    Root<ForumTopicRead> root = criteria.from(ForumTopicRead.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicRead_.topic), forumTopic));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<ForumTopicRead> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicRead> criteria = criteriaBuilder.createQuery(ForumTopicRead.class);
    Root<ForumTopicRead> root = criteria.from(ForumTopicRead.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicRead_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long countByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ForumTopicRead> root = criteria.from(ForumTopicRead.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicRead_.user), user));

    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public ForumTopicRead updateTime(ForumTopicRead forumTopicRead, Date time) {
    forumTopicRead.setTime(time);
    return persist(forumTopicRead);
  }

}

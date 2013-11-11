package fi.foyt.fni.persistence.dao.forum;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.forum.ForumTopicFollower;
import fi.foyt.fni.persistence.model.forum.ForumTopicFollower_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ForumTopicFollowerDAO extends GenericDAO<ForumTopicFollower> {

	private static final long serialVersionUID = 1L;

	public ForumTopicFollower create(ForumTopic topic, User user) {
	  ForumTopicFollower forumTopicFollower = new ForumTopicFollower();
	  forumTopicFollower.setTopic(topic);
	  forumTopicFollower.setUser(user);
	  return persist(forumTopicFollower);
	}

  public List<ForumTopicFollower> listByForumTopic(ForumTopic forumTopic) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicFollower> criteria = criteriaBuilder.createQuery(ForumTopicFollower.class);
    Root<ForumTopicFollower> root = criteria.from(ForumTopicFollower.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicFollower_.topic), forumTopic));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<ForumTopicFollower> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicFollower> criteria = criteriaBuilder.createQuery(ForumTopicFollower.class);
    Root<ForumTopicFollower> root = criteria.from(ForumTopicFollower.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicFollower_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }

}

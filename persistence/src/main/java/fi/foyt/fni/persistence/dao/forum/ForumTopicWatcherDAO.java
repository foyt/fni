package fi.foyt.fni.persistence.dao.forum;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.forum.ForumTopicWatcher;
import fi.foyt.fni.persistence.model.forum.ForumTopicWatcher_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ForumTopicWatcherDAO extends GenericDAO<ForumTopicWatcher> {

	private static final long serialVersionUID = 1L;

	public ForumTopicWatcher create(ForumTopic topic, User user) {
	  ForumTopicWatcher forumTopicWatcher = new ForumTopicWatcher();
	  forumTopicWatcher.setTopic(topic);
	  forumTopicWatcher.setUser(user);
	  return persist(forumTopicWatcher);
	}

  public List<ForumTopicWatcher> listByForumTopic(ForumTopic forumTopic) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicWatcher> criteria = criteriaBuilder.createQuery(ForumTopicWatcher.class);
    Root<ForumTopicWatcher> root = criteria.from(ForumTopicWatcher.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicWatcher_.topic), forumTopic));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<ForumTopicWatcher> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumTopicWatcher> criteria = criteriaBuilder.createQuery(ForumTopicWatcher.class);
    Root<ForumTopicWatcher> root = criteria.from(ForumTopicWatcher.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumTopicWatcher_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }

}

package fi.foyt.fni.persistence.dao.forum;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumFollower;
import fi.foyt.fni.persistence.model.forum.ForumFollower_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ForumFollowerDAO extends GenericDAO<ForumFollower> {

	private static final long serialVersionUID = 1L;

	public ForumFollower create(Forum forum, User user) {
	  ForumFollower forumFollower = new ForumFollower();
	  forumFollower.setForum(forum);
	  forumFollower.setUser(user);
	  return persist(forumFollower);
	}

  public List<ForumFollower> listByForum(Forum forum) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumFollower> criteria = criteriaBuilder.createQuery(ForumFollower.class);
    Root<ForumFollower> root = criteria.from(ForumFollower.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumFollower_.forum), forum));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<ForumFollower> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumFollower> criteria = criteriaBuilder.createQuery(ForumFollower.class);
    Root<ForumFollower> root = criteria.from(ForumFollower.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumFollower_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }

}

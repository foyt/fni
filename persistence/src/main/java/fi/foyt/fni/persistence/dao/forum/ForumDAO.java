package fi.foyt.fni.persistence.dao.forum;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.forum.Forum_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;

@DAO
public class ForumDAO extends GenericDAO<Forum> {

	private static final long serialVersionUID = 1L;

	public Forum create(String name, String urlName, String description, ForumCategory forumCategory) {
    EntityManager entityManager = getEntityManager();

    Forum forum = new Forum();
    forum.setName(name);
    forum.setUrlName(urlName);
    forum.setCategory(forumCategory);
    forum.setDescription(description);

    entityManager.persist(forum);

    return forum;
  }

  public Forum findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Forum> criteria = criteriaBuilder.createQuery(Forum.class);
    Root<Forum> root = criteria.from(Forum.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Forum_.name), name));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public Forum findByUrlName(String urlName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Forum> criteria = criteriaBuilder.createQuery(Forum.class);
    Root<Forum> root = criteria.from(Forum.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Forum_.urlName), urlName));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<Forum> listByCategory(ForumCategory category) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Forum> criteria = criteriaBuilder.createQuery(Forum.class);
    Root<Forum> root = criteria.from(Forum.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Forum_.category), category));

    return entityManager.createQuery(criteria).getResultList();
  }  
  
}

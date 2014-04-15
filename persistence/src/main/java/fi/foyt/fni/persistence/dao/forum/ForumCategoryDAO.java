package fi.foyt.fni.persistence.dao.forum;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.forum.ForumCategory_;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.forum.ForumCategory;

public class ForumCategoryDAO extends GenericDAO<ForumCategory> {

	private static final long serialVersionUID = 1L;

	public ForumCategory create(String name) {
    EntityManager entityManager = getEntityManager();
    ForumCategory forumCategory = new ForumCategory();
    forumCategory.setName(name);

    entityManager.persist(forumCategory);

    return forumCategory;
  }

  public ForumCategory findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ForumCategory> criteria = criteriaBuilder.createQuery(ForumCategory.class);
    Root<ForumCategory> root = criteria.from(ForumCategory.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(ForumCategory_.name), name));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}

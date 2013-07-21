package fi.foyt.fni.persistence.dao.blog;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.blog.BlogTag;
import fi.foyt.fni.persistence.model.blog.BlogTag_;

@DAO
public class BlogTagDAO extends GenericDAO<BlogTag> {

	private static final long serialVersionUID = 1L;

	public BlogTag create(String text) {
		BlogTag blogTag = new BlogTag();
		blogTag.setText(text);
		getEntityManager().persist(blogTag);
		return blogTag;
	}

	public BlogTag findByText(String text) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogTag> criteria = criteriaBuilder.createQuery(BlogTag.class);
    Root<BlogTag> root = criteria.from(BlogTag.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(BlogTag_.text), text)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

}

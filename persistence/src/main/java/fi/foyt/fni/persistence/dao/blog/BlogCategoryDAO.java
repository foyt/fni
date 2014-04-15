package fi.foyt.fni.persistence.dao.blog;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.blog.BlogCategory;
import fi.foyt.fni.persistence.model.blog.BlogCategorySync;
import fi.foyt.fni.persistence.model.blog.BlogCategory_;

public class BlogCategoryDAO extends GenericDAO<BlogCategory> {

	private static final long serialVersionUID = 1L;

	public BlogCategory create(String name, String syncUrl, BlogCategorySync sync, Date nextSync) {
		BlogCategory blogCategory = new BlogCategory();
		blogCategory.setName(name);
		blogCategory.setNextSync(nextSync);
		blogCategory.setSync(sync);
		blogCategory.setSyncUrl(syncUrl);
		getEntityManager().persist(blogCategory);
		return blogCategory;
	}
	
	public List<BlogCategory> listBySyncNeNullAndNextSyncLe(Date nextSync) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogCategory> criteria = criteriaBuilder.createQuery(BlogCategory.class);
    Root<BlogCategory> root = criteria.from(BlogCategory.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
  			criteriaBuilder.isNotNull(root.get(BlogCategory_.sync)),
  	  	criteriaBuilder.lessThanOrEqualTo(root.get(BlogCategory_.nextSync), nextSync)
  	  )
    );

    return entityManager.createQuery(criteria).getResultList();
	}

	public BlogCategory updateNextSync(BlogCategory blogCategory, Date nextSync) {
		blogCategory.setNextSync(nextSync);
		getEntityManager().persist(blogCategory);
		return blogCategory;
	}
	
}

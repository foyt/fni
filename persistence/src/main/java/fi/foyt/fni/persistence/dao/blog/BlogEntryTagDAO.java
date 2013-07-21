package fi.foyt.fni.persistence.dao.blog;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogEntryTag;
import fi.foyt.fni.persistence.model.blog.BlogEntryTag_;
import fi.foyt.fni.persistence.model.blog.BlogTag;

@DAO
public class BlogEntryTagDAO extends GenericDAO<BlogEntryTag> {

	private static final long serialVersionUID = 1L;

	public BlogEntryTag create(BlogEntry entry, BlogTag tag) {
		BlogEntryTag blogEntryTag = new BlogEntryTag();
		
		blogEntryTag.setEntry(entry);
		blogEntryTag.setTag(tag);
		
		getEntityManager().persist(blogEntryTag);
		
		return blogEntryTag;
	}

	public BlogEntryTag findByEntryAndTag(BlogEntry entry, BlogTag tag) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogEntryTag> criteria = criteriaBuilder.createQuery(BlogEntryTag.class);
    Root<BlogEntryTag> root = criteria.from(BlogEntryTag.class);
    criteria.select(root);
    criteria.where(
   		criteriaBuilder.and(
    		  criteriaBuilder.equal(root.get(BlogEntryTag_.entry), entry),
    		  criteriaBuilder.equal(root.get(BlogEntryTag_.tag), tag)
  		)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

	public List<BlogTag> listTagsByBlogEntry(BlogEntry entry) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogTag> criteria = criteriaBuilder.createQuery(BlogTag.class);
    Root<BlogEntryTag> root = criteria.from(BlogEntryTag.class);
    criteria.select(root.get(BlogEntryTag_.tag));
    criteria.where(
      criteriaBuilder.equal(root.get(BlogEntryTag_.entry), entry)
    );

    return entityManager.createQuery(criteria).getResultList();
	}

	public List<BlogEntryTag> listByEntryAndTagNotIn(BlogEntry entry, List<BlogTag> blogTags) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogEntryTag> criteria = criteriaBuilder.createQuery(BlogEntryTag.class);
    Root<BlogEntryTag> root = criteria.from(BlogEntryTag.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.and(
        criteriaBuilder.equal(root.get(BlogEntryTag_.entry), entry),
        criteriaBuilder.not(root.get(BlogEntryTag_.tag).in(blogTags))
	    )
    );

    return entityManager.createQuery(criteria).getResultList();
	}
	
	

}

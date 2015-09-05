package fi.foyt.fni.persistence.dao.blog;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.blog.BlogCategory;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogEntry_;
import fi.foyt.fni.persistence.model.users.User;

public class BlogEntryDAO extends GenericDAO<BlogEntry> {

	private static final long serialVersionUID = 1L;

	public BlogEntry create(String guid, BlogCategory category, String urlName, String authorName, String link, String title, String summary, String content, Date modified, User modifier, Date created, User creator) {
		BlogEntry blogEntry = new BlogEntry();
		
		blogEntry.setGuid(guid);
		blogEntry.setAuthorName(authorName);
		blogEntry.setLink(link);
		blogEntry.setTitle(title);
		blogEntry.setSummary(summary);
		blogEntry.setContent(content);
		blogEntry.setCategory(category);
		blogEntry.setModified(modified);
		blogEntry.setModifier(modifier);
		blogEntry.setCreated(created);
		blogEntry.setCreator(creator);
		blogEntry.setUrlName(urlName);
		
		getEntityManager().persist(blogEntry);
		
		return blogEntry;
	}

	public BlogEntry findByGuid(String guid) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogEntry> criteria = criteriaBuilder.createQuery(BlogEntry.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(BlogEntry_.guid), guid)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

	public List<BlogEntry> listByCategory(BlogCategory category) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogEntry> criteria = criteriaBuilder.createQuery(BlogEntry.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(BlogEntry_.category), category)
    );

    return entityManager.createQuery(criteria).getResultList();
	}

	public List<BlogEntry> listAllSortByCreated(int firstResult, int maxResults) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogEntry> criteria = criteriaBuilder.createQuery(BlogEntry.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(BlogEntry_.created)));
    TypedQuery<BlogEntry> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
	}

  public List<BlogEntry> listByCreatedGreaterOrEqualAndCreatedLessOrEqualSortByCreated(Date greater, Date less) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<BlogEntry> criteria = criteriaBuilder.createQuery(BlogEntry.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.greaterThanOrEqualTo(root.get(BlogEntry_.created), greater),
        criteriaBuilder.lessThanOrEqualTo(root.get(BlogEntry_.created), less)
      )
    );
    
    criteria.orderBy(criteriaBuilder.desc(root.get(BlogEntry_.created)));
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Date minBlogDate() {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Date> criteria = criteriaBuilder.createQuery(Date.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(criteriaBuilder.least(root.get(BlogEntry_.created)));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public Date maxBlogDate() {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Date> criteria = criteriaBuilder.createQuery(Date.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(criteriaBuilder.greatest(root.get(BlogEntry_.created)));
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public Long countByCreatedGreaterOrEqualAndCreatedLessOrEqualSortByCreated(Date greater, Date less) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<BlogEntry> root = criteria.from(BlogEntry.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.greaterThanOrEqualTo(root.get(BlogEntry_.created), greater),
        criteriaBuilder.lessThanOrEqualTo(root.get(BlogEntry_.created), less)
      )
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

	public BlogEntry updateAuthorName(BlogEntry blogEntry, String authorName) {
		blogEntry.setAuthorName(authorName);
		getEntityManager().persist(blogEntry);
		return blogEntry;
	}

	public BlogEntry updateLink(BlogEntry blogEntry, String link) {
		blogEntry.setLink(link);
		getEntityManager().persist(blogEntry);
		return blogEntry;
	}

	public BlogEntry updateTitle(BlogEntry blogEntry, String title) {
		blogEntry.setTitle(title);
		getEntityManager().persist(blogEntry);
		return blogEntry;
	}

	public BlogEntry updateSummary(BlogEntry blogEntry, String summary) {
		blogEntry.setSummary(summary);
		getEntityManager().persist(blogEntry);
		return blogEntry;
	}
	
	public BlogEntry updateModified(BlogEntry blogEntry, Date modified) {
		blogEntry.setModified(modified);
		getEntityManager().persist(blogEntry);
		return blogEntry;
	}
	
}

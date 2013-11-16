package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.Publication_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class PublicationDAO extends GenericDAO<Publication> {
  
	private static final long serialVersionUID = 1L;

	public Publication findByUrlName(String urlName) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Publication> criteria = criteriaBuilder.createQuery(Publication.class);
    Root<Publication> root = criteria.from(Publication.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Publication_.urlName), urlName)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
	}

	public List<Publication> listByPublishedOrderByCreated(Boolean published, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Publication> criteria = criteriaBuilder.createQuery(Publication.class);
    Root<Publication> root = criteria.from(Publication.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Publication_.published), published)
    );
    
    
    criteria.orderBy(criteriaBuilder.desc(root.get(Publication_.created)));

    TypedQuery<Publication> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

	public List<Publication> listByPublished(Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Publication> criteria = criteriaBuilder.createQuery(Publication.class);
    Root<Publication> root = criteria.from(Publication.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Publication_.published), published)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public List<Publication> listByCreatorAndPublished(User creator, Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Publication> criteria = criteriaBuilder.createQuery(Publication.class);
    Root<Publication> root = criteria.from(Publication.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.and(
  		  criteriaBuilder.equal(root.get(Publication_.creator), creator),
  			criteriaBuilder.equal(root.get(Publication_.published), published)
  		)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public Long countByCreatorAndPublished(User creator, Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<Publication> root = criteria.from(Publication.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
  		criteriaBuilder.and(
  		  criteriaBuilder.equal(root.get(Publication_.creator), creator),
  			criteriaBuilder.equal(root.get(Publication_.published), published)
  		)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
	}

	public Publication updateName(Publication publication, String name) {
		publication.setName(name);
		getEntityManager().persist(publication);
		return publication;
	}

  public Publication updateUrlName(Publication publication, String urlName) {
    publication.setUrlName(urlName);
    return persist(publication);
  }

	public Publication updateDescription(Publication publication, String description) {
		publication.setDescription(description);
		getEntityManager().persist(publication);
		return publication;
	}

	public Publication updateDefaultImage(Publication publication, PublicationImage defaultImage) {
		publication.setDefaultImage(defaultImage);
		getEntityManager().persist(publication);
		return publication;
	}

	public Publication updatePrice(Publication publication, Double price) {
		publication.setPrice(price);
		getEntityManager().persist(publication);
		return publication;
	}

	public Publication updatePublished(Publication publication, Boolean published) {
		publication.setPublished(published);
		getEntityManager().persist(publication);
		return publication;
	}

	public Publication updateModified(Publication publication, Date modified) {
		publication.setModified(modified);
		getEntityManager().persist(publication);
		return publication;
	}
	
	public Publication updateModifier(Publication publication, User modifier) {
		publication.setModifier(modifier);
		getEntityManager().persist(publication);
		return publication;
	}

	public Publication updateWidth(Publication publication, Integer width) {
		publication.setWidth(width);
		getEntityManager().persist(publication);
    return publication;
	}

	public Publication updateHeight(Publication publication, Integer height) {
		publication.setHeight(height);
		getEntityManager().persist(publication);
    return publication;
	}

	public Publication updateDepth(Publication publication, Integer depth) {
		publication.setDepth(depth);
		getEntityManager().persist(publication);
    return publication;
	}

	public Publication updateWeight(Publication publication, Double weight) {
		publication.setWeight(weight);
		getEntityManager().persist(publication);
    return publication;
	}

	public Publication updateLicense(Publication publication, String license) {
		publication.setLicense(license);
		return persist(publication);
	}

	public Publication updateForumTopic(Publication publication, ForumTopic forumTopic) {
		publication.setForumTopic(forumTopic);
		return persist(publication);
	}

  public Publication updateLanguage(Publication publication, Language language) {
    publication.setLanguage(language);
    return persist(publication);
  }

}

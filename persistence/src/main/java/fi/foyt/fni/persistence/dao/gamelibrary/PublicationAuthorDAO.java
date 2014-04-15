package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor_;
import fi.foyt.fni.persistence.model.users.User;

public class PublicationAuthorDAO extends GenericDAO<PublicationAuthor> {
  
	private static final long serialVersionUID = 1L;

	public PublicationAuthor create(Publication publication, User author) {
		PublicationAuthor publicationAuthor = new PublicationAuthor();
		publicationAuthor.setAuthor(author);
		publicationAuthor.setPublication(publication);
		return persist(publicationAuthor);
	}

	public PublicationAuthor findByPublicationAndAuthor(Publication publication, User author) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicationAuthor> criteria = criteriaBuilder.createQuery(PublicationAuthor.class);
    Root<PublicationAuthor> root = criteria.from(PublicationAuthor.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
      		criteriaBuilder.equal(root.get(PublicationAuthor_.publication), publication),
      		criteriaBuilder.equal(root.get(PublicationAuthor_.author), author)
    	)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
	}

	public List<PublicationAuthor> listByPublication(Publication publication) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicationAuthor> criteria = criteriaBuilder.createQuery(PublicationAuthor.class);
    Root<PublicationAuthor> root = criteria.from(PublicationAuthor.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(PublicationAuthor_.publication), publication)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public List<PublicationAuthor> listByAuthor(User author) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicationAuthor> criteria = criteriaBuilder.createQuery(PublicationAuthor.class);
    Root<PublicationAuthor> root = criteria.from(PublicationAuthor.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(PublicationAuthor_.author), author)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public List<Publication> listPublicationsByAuthor(User author) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Publication> criteria = criteriaBuilder.createQuery(Publication.class);
    Root<PublicationAuthor> root = criteria.from(PublicationAuthor.class);
    criteria.select(root.get(PublicationAuthor_.publication));
    criteria.where(
    		criteriaBuilder.equal(root.get(PublicationAuthor_.author), author)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public List<PublicationAuthor> listByPublicationAndAuthorNotIn(Publication publication, List<User> authors) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicationAuthor> criteria = criteriaBuilder.createQuery(PublicationAuthor.class);
    Root<PublicationAuthor> root = criteria.from(PublicationAuthor.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(PublicationAuthor_.publication), publication),
    		criteriaBuilder.not(root.get(PublicationAuthor_.author).in(authors))
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
	
}

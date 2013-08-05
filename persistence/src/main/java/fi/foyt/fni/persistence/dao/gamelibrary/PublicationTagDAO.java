package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag_;
import fi.foyt.fni.persistence.model.gamelibrary.Publication_;

@DAO
public class PublicationTagDAO extends GenericDAO<PublicationTag> {
  
	private static final long serialVersionUID = 1L;

	public PublicationTag create(GameLibraryTag tag, Publication publication) {
		PublicationTag publicationTag = new PublicationTag();
		publicationTag.setProduct(publication);
		publicationTag.setTag(tag);
		getEntityManager().persist(publicationTag);
		return publicationTag;
	}

	public List<PublicationTag> listByProduct(Publication publication) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicationTag> criteria = criteriaBuilder.createQuery(PublicationTag.class);
    Root<PublicationTag> root = criteria.from(PublicationTag.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(PublicationTag_.publication), publication)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<Publication> listProductsByGameLibraryTags(List<GameLibraryTag> gameLibaryTags) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Publication> criteria = criteriaBuilder.createQuery(Publication.class);
    Root<PublicationTag> root = criteria.from(PublicationTag.class);
    criteria.select(root.get(PublicationTag_.publication));
    criteria.where(
    	root.get(PublicationTag_.tag).in(gameLibaryTags)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public Long countProductsByTag(GameLibraryTag tag) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<PublicationTag> root = criteria.from(PublicationTag.class);
    criteria.select(criteriaBuilder.count(root.get(PublicationTag_.publication)));
    criteria.where(
    		criteriaBuilder.equal(root.get(PublicationTag_.tag), tag)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
	}

	public List<GameLibraryTag> listGameLibraryTagsByProductPublished(Boolean published) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GameLibraryTag> criteria = criteriaBuilder.createQuery(GameLibraryTag.class);
    Root<PublicationTag> root = criteria.from(PublicationTag.class);
    Join<PublicationTag, Publication> join = root.join(PublicationTag_.publication);
    criteria.select(root.get(PublicationTag_.tag)).distinct(true);
    
    criteria.where(
    		criteriaBuilder.and(
    		  criteriaBuilder.equal(join.get(Publication_.published), published)
    		)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
}

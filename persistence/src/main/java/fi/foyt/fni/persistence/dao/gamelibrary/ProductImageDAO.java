package fi.foyt.fni.persistence.dao.gamelibrary;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.ProductImage_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class ProductImageDAO extends GenericDAO<PublicationImage> {
  
	private static final long serialVersionUID = 1L;

	public PublicationImage create(Publication publication, byte[] content, String contentType, Date created, Date modified, User creator, User modifier) {
		PublicationImage publicationImage = new PublicationImage();
		publicationImage.setContent(content);
		publicationImage.setContentType(contentType);
		publicationImage.setCreated(created);
		publicationImage.setCreator(creator);
		publicationImage.setModified(modified);
		publicationImage.setModifier(modifier);
		publicationImage.setProduct(publication);
		
		getEntityManager().persist(publicationImage);
		
		return publicationImage;
	}

	public List<PublicationImage> listByProduct(Publication publication) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublicationImage> criteria = criteriaBuilder.createQuery(PublicationImage.class);
    Root<PublicationImage> root = criteria.from(PublicationImage.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(ProductImage_.publication), publication)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
	
}

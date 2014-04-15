package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.ImageRevision;
import fi.foyt.fni.persistence.model.materials.ImageRevision_;

public class ImageRevisionDAO extends GenericDAO<ImageRevision> {

	private static final long serialVersionUID = 1L;

	public ImageRevision create(Image image, Long revision, Date created, Boolean compressed, Boolean completeRevision, byte[] data, String checksum, String sessionId) {
    ImageRevision imageRevision = new ImageRevision();
    imageRevision.setCreated(created);
    imageRevision.setCompleteRevision(completeRevision);
    imageRevision.setCompressed(compressed);
    imageRevision.setCreated(created);
    imageRevision.setData(data);
    imageRevision.setImage(image);
    imageRevision.setRevision(revision);
    imageRevision.setChecksum(checksum);
    imageRevision.setSessionId(sessionId);
    
    return persist(imageRevision);
  }
	
	public List<ImageRevision> listByImage(Image image) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ImageRevision> criteria = criteriaBuilder.createQuery(ImageRevision.class);
    Root<ImageRevision> root = criteria.from(ImageRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(ImageRevision_.image), image)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public  List<ImageRevision> listByImageAndRevisionGreaterThan(Image image, Long revision) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<ImageRevision> criteria = criteriaBuilder.createQuery(ImageRevision.class);
    Root<ImageRevision> root = criteria.from(ImageRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(ImageRevision_.image), image),
    		criteriaBuilder.greaterThan(root.get(ImageRevision_.revision), revision)
    	)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long maxRevisionByImage(Image image) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<ImageRevision> root = criteria.from(ImageRevision.class);
    criteria.select(criteriaBuilder.max(root.get(ImageRevision_.revision)));
    criteria.where(
      criteriaBuilder.equal(root.get(ImageRevision_.image), image)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}

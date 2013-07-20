package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.materials.VectorImageRevision;
import fi.foyt.fni.persistence.model.materials.VectorImageRevision_;

@DAO
public class VectorImageRevisionDAO extends GenericDAO<VectorImageRevision> {

	private static final long serialVersionUID = 1L;

	public VectorImageRevision create(VectorImage vectorImage, Long revision, Date created, Boolean compressed, Boolean completeRevision, byte[] data, String title, Language language) {
    EntityManager entityManager = getEntityManager();

    VectorImageRevision vectorImageRevision = new VectorImageRevision();
    vectorImageRevision.setCreated(created);
    vectorImageRevision.setCompleteRevision(completeRevision);
    vectorImageRevision.setCompressed(compressed);
    vectorImageRevision.setCreated(created);
    vectorImageRevision.setData(data);
    vectorImageRevision.setTitle(title);
    vectorImageRevision.setLanguage(language);
    vectorImageRevision.setVectorImage(vectorImage);
    vectorImageRevision.setRevision(revision);
    
    entityManager.persist(vectorImageRevision);

    return vectorImageRevision;
  }
	
	public List<VectorImageRevision> listByVectorImage(VectorImage vectorImage) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<VectorImageRevision> criteria = criteriaBuilder.createQuery(VectorImageRevision.class);
    Root<VectorImageRevision> root = criteria.from(VectorImageRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(VectorImageRevision_.vectorImage), vectorImage)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}
  
  public Long maxRevisionByVectorImage(VectorImage vectorImage) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<VectorImageRevision> root = criteria.from(VectorImageRevision.class);
    criteria.select(criteriaBuilder.max(root.get(VectorImageRevision_.revision)));
    criteria.where(
      criteriaBuilder.equal(root.get(VectorImageRevision_.vectorImage), vectorImage)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
}

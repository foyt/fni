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
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.DocumentRevision;
import fi.foyt.fni.persistence.model.materials.DocumentRevision_;

@DAO
public class DocumentRevisionDAO extends GenericDAO<DocumentRevision> {

	private static final long serialVersionUID = 1L;

	public DocumentRevision create(Document document, Long revision, Date created, Boolean compressed, Boolean completeRevision, byte[] data, String checksum, String title, Language language, String clientId) {
    DocumentRevision documentRevision = new DocumentRevision();
    documentRevision.setCreated(created);
    documentRevision.setCompleteRevision(completeRevision);
    documentRevision.setCompressed(compressed);
    documentRevision.setCreated(created);
    documentRevision.setData(data);
    documentRevision.setTitle(title);
    documentRevision.setLanguage(language);
    documentRevision.setDocument(document);
    documentRevision.setRevision(revision);
    documentRevision.setChecksum(checksum);
    documentRevision.setClientId(clientId);
    
    return persist(documentRevision);
  }
	
	public List<DocumentRevision> listByDocument(Document document) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DocumentRevision> criteria = criteriaBuilder.createQuery(DocumentRevision.class);
    Root<DocumentRevision> root = criteria.from(DocumentRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(DocumentRevision_.document), document)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public  List<DocumentRevision> listByDocumentAndRevisionGreaterThan(Document document, Long revision) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DocumentRevision> criteria = criteriaBuilder.createQuery(DocumentRevision.class);
    Root<DocumentRevision> root = criteria.from(DocumentRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(DocumentRevision_.document), document),
    		criteriaBuilder.greaterThan(root.get(DocumentRevision_.revision), revision)
    	)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long maxRevisionByDocument(Document document) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<DocumentRevision> root = criteria.from(DocumentRevision.class);
    criteria.select(criteriaBuilder.max(root.get(DocumentRevision_.revision)));
    criteria.where(
      criteriaBuilder.equal(root.get(DocumentRevision_.document), document)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
  
  public DocumentRevision updateTitle(DocumentRevision documentRevision, String title) {
    EntityManager entityManager = getEntityManager();
	
    documentRevision.setTitle(title);
    
    entityManager.persist(documentRevision);
    
    return documentRevision;
  }
  
  public DocumentRevision updateLanguage(DocumentRevision documentRevision, Language language) {
    EntityManager entityManager = getEntityManager();
	
    documentRevision.setLanguage(language);
    
    entityManager.persist(documentRevision);
    
    return documentRevision;
  }
}

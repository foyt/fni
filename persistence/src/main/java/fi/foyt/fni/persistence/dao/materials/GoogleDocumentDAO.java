package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.GoogleDocument;
import fi.foyt.fni.persistence.model.materials.GoogleDocument_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class GoogleDocumentDAO extends GenericDAO<GoogleDocument> {

	private static final long serialVersionUID = 1L;

	public GoogleDocument create(User creator, Language language, Folder parentFolder,  String urlName, String title, String documentId, String mimeType, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date();

    GoogleDocument googleDocument = new GoogleDocument();
    googleDocument.setDocumentId(documentId);
    googleDocument.setCreated(now);
    googleDocument.setCreator(creator);
    googleDocument.setModified(now);
    googleDocument.setModifier(creator);
    googleDocument.setTitle(title);
    googleDocument.setUrlName(urlName);
    googleDocument.setMimeType(mimeType);
    googleDocument.setPublicity(publicity);

    if (language != null)
      googleDocument.setLanguage(language);

    if (parentFolder != null)
      googleDocument.setParentFolder(parentFolder);

    entityManager.persist(googleDocument);

    return googleDocument;
  }
  
  public GoogleDocument findByCreatorAndDocumentId(User creator, String documentId) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<GoogleDocument> criteria = criteriaBuilder.createQuery(GoogleDocument.class);
    Root<GoogleDocument> root = criteria.from(GoogleDocument.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(GoogleDocument_.creator), creator),
          criteriaBuilder.equal(root.get(GoogleDocument_.documentId), documentId)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
}

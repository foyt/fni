package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocument_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionEventDocumentDAO extends GenericDAO<IllusionEventDocument> {

  private static final long serialVersionUID = -4644199519384824575L;

  public IllusionEventDocument create(User creator, IllusionEventDocumentType documentType, Language language, Folder parentFolder, String urlName, String title, String data, MaterialPublicity publicity) {
    Date now = new Date();

    IllusionEventDocument illusionEventDocument = new IllusionEventDocument();
    illusionEventDocument.setData(data);
    illusionEventDocument.setCreated(now);
    illusionEventDocument.setCreator(creator);
    illusionEventDocument.setModified(now);
    illusionEventDocument.setModifier(creator);
    illusionEventDocument.setTitle(title);
    illusionEventDocument.setUrlName(urlName);
    illusionEventDocument.setPublicity(publicity);
    illusionEventDocument.setDocumentType(documentType);
    illusionEventDocument.setLanguage(language);
    illusionEventDocument.setParentFolder(parentFolder);

    return persist(illusionEventDocument);
  }

  public IllusionEventDocument findByParentFolderAndDocumentType(Folder parentFolder, IllusionEventDocumentType documentType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventDocument> criteria = criteriaBuilder.createQuery(IllusionEventDocument.class);
    Root<IllusionEventDocument> root = criteria.from(IllusionEventDocument.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventDocument_.parentFolder), parentFolder),
        criteriaBuilder.equal(root.get(IllusionEventDocument_.documentType), documentType)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventDocument> listByParentFolderAndDocumentType(Folder parentFolder, IllusionEventDocumentType documentType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventDocument> criteria = criteriaBuilder.createQuery(IllusionEventDocument.class);
    Root<IllusionEventDocument> root = criteria.from(IllusionEventDocument.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventDocument_.parentFolder), parentFolder),
        criteriaBuilder.equal(root.get(IllusionEventDocument_.documentType), documentType)
      )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
}

package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument;
import fi.foyt.fni.persistence.model.materials.IllusionEventDocumentType;
import fi.foyt.fni.persistence.model.materials.IllusionGroupDocument_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionGroupDocumentDAO extends GenericDAO<IllusionGroupDocument> {

  private static final long serialVersionUID = -4644199519384824575L;

  public IllusionGroupDocument create(User creator, IllusionEventDocumentType documentType, Language language, Folder parentFolder, String urlName, String title, String data, MaterialPublicity publicity) {
    Date now = new Date();

    IllusionGroupDocument illusionGroupDocument = new IllusionGroupDocument();
    illusionGroupDocument.setData(data);
    illusionGroupDocument.setCreated(now);
    illusionGroupDocument.setCreator(creator);
    illusionGroupDocument.setModified(now);
    illusionGroupDocument.setModifier(creator);
    illusionGroupDocument.setTitle(title);
    illusionGroupDocument.setUrlName(urlName);
    illusionGroupDocument.setPublicity(publicity);
    illusionGroupDocument.setDocumentType(documentType);
    illusionGroupDocument.setLanguage(language);
    illusionGroupDocument.setParentFolder(parentFolder);

    return persist(illusionGroupDocument);
  }

  public IllusionGroupDocument findByParentFolderAndDocumentType(Folder parentFolder, IllusionEventDocumentType documentType) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupDocument> criteria = criteriaBuilder.createQuery(IllusionGroupDocument.class);
    Root<IllusionGroupDocument> root = criteria.from(IllusionGroupDocument.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupDocument_.parentFolder), parentFolder),
        criteriaBuilder.equal(root.get(IllusionGroupDocument_.documentType), documentType)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
}

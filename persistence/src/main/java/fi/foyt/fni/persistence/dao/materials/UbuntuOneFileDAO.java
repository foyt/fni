package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFile_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class UbuntuOneFileDAO extends GenericDAO<UbuntuOneFile> {

  public UbuntuOneFile create(User creator, Language language, Folder parentFolder, String urlName, String title, MaterialPublicity publicity, String ubuntuOneKey,
      Long generation, String contentPath, String mimeType) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date();

    UbuntuOneFile ubuntuOneFile = new UbuntuOneFile();
    ubuntuOneFile.setCreated(now);
    ubuntuOneFile.setCreator(creator);
    ubuntuOneFile.setModified(now);
    ubuntuOneFile.setModifier(creator);
    ubuntuOneFile.setTitle(title);
    ubuntuOneFile.setUrlName(urlName);
    ubuntuOneFile.setPublicity(publicity);
    ubuntuOneFile.setLanguage(language);
    ubuntuOneFile.setParentFolder(parentFolder);
    ubuntuOneFile.setUbuntuOneKey(ubuntuOneKey);
    ubuntuOneFile.setGeneration(generation);
    ubuntuOneFile.setContentPath(contentPath);
    ubuntuOneFile.setMimeType(mimeType);

    entityManager.persist(ubuntuOneFile);

    return ubuntuOneFile;
  }

  public UbuntuOneFile findByUbuntuOneKey(String ubuntuOneKey) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UbuntuOneFile> criteria = criteriaBuilder.createQuery(UbuntuOneFile.class);
    Root<UbuntuOneFile> root = criteria.from(UbuntuOneFile.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UbuntuOneFile_.ubuntuOneKey), ubuntuOneKey));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public UbuntuOneFile findByContentPath(String contentPath) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UbuntuOneFile> criteria = criteriaBuilder.createQuery(UbuntuOneFile.class);
    Root<UbuntuOneFile> root = criteria.from(UbuntuOneFile.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UbuntuOneFile_.contentPath), contentPath));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<String> listUbuntuOneKeysByParentFolderAndCreator(Folder parentFolder, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteria = criteriaBuilder.createQuery(String.class);
    Root<UbuntuOneFile> root = criteria.from(UbuntuOneFile.class);
    criteria.select(root.get(UbuntuOneFile_.ubuntuOneKey));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UbuntuOneFile_.parentFolder), parentFolder),
        criteriaBuilder.equal(root.get(UbuntuOneFile_.creator), user)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public UbuntuOneFile updateGeneration(UbuntuOneFile ubuntuOneFile, Long generation, User modifier) {
    ubuntuOneFile.setGeneration(generation);
    ubuntuOneFile.setModifier(modifier);
    getEntityManager().persist(ubuntuOneFile);
    return ubuntuOneFile;
  }

  public UbuntuOneFile updateContentPath(UbuntuOneFile ubuntuOneFile, String contentPath, User modifier) {
    ubuntuOneFile.setContentPath(contentPath);
    ubuntuOneFile.setModifier(modifier);
    getEntityManager().persist(ubuntuOneFile);
    return ubuntuOneFile;
  }
}

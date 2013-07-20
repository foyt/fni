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
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFolder;
import fi.foyt.fni.persistence.model.materials.UbuntuOneFolder_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class UbuntuOneFolderDAO extends GenericDAO<UbuntuOneFolder> {

  private static final long serialVersionUID = 1L;

	public UbuntuOneFolder create(User creator, Language language, Folder parentFolder, String urlName, String title, MaterialPublicity publicity, String ubuntuOneKey,
      Long generation, String contentPath) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date();

    UbuntuOneFolder ubuntuOneFile = new UbuntuOneFolder();
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

    entityManager.persist(ubuntuOneFile);

    return ubuntuOneFile;
  }

  public UbuntuOneFolder findByUbuntuOneKey(String ubuntuOneKey) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UbuntuOneFolder> criteria = criteriaBuilder.createQuery(UbuntuOneFolder.class);
    Root<UbuntuOneFolder> root = criteria.from(UbuntuOneFolder.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UbuntuOneFolder_.ubuntuOneKey), ubuntuOneKey));

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public UbuntuOneFolder findByCreatorAndContentPath(User creator, String contentPath) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UbuntuOneFolder> criteria = criteriaBuilder.createQuery(UbuntuOneFolder.class);
    Root<UbuntuOneFolder> root = criteria.from(UbuntuOneFolder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UbuntuOneFolder_.contentPath), contentPath),
        criteriaBuilder.equal(root.get(UbuntuOneFolder_.creator), creator)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<String> listUbuntuOneKeysByParentFolderAndCreator(Folder parentFolder, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<String> criteria = criteriaBuilder.createQuery(String.class);
    Root<UbuntuOneFolder> root = criteria.from(UbuntuOneFolder.class);
    criteria.select(root.get(UbuntuOneFolder_.ubuntuOneKey));
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(UbuntuOneFolder_.parentFolder), parentFolder),
        criteriaBuilder.equal(root.get(UbuntuOneFolder_.creator), user)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public UbuntuOneFolder updateGeneration(UbuntuOneFolder ubuntuOneFolder, Long generation, User modifier) {
    ubuntuOneFolder.setGeneration(generation);
    ubuntuOneFolder.setModifier(modifier);
    getEntityManager().persist(ubuntuOneFolder);
    return ubuntuOneFolder;
  }

  public UbuntuOneFolder updateContentPath(UbuntuOneFolder ubuntuOneFolder, String contentPath, User modifier) {
    ubuntuOneFolder.setContentPath(contentPath);
    ubuntuOneFolder.setModifier(modifier);
    getEntityManager().persist(ubuntuOneFolder);
    return ubuntuOneFolder;
  }

}

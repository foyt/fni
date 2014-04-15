package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.DropboxFolder;
import fi.foyt.fni.persistence.model.materials.DropboxFolder_;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class DropboxFolderDAO extends GenericDAO<DropboxFolder> {

	private static final long serialVersionUID = 1L;

	public DropboxFolder create(User creator, Date created, User modifier, Date modified, Folder parentFolder, String urlName, String title, MaterialPublicity publicity, String dropboxPath) {
    EntityManager entityManager = getEntityManager();

    DropboxFolder dropboxFolder = new DropboxFolder();
    dropboxFolder.setCreated(created);
    dropboxFolder.setCreator(creator);
    dropboxFolder.setModified(modified);
    dropboxFolder.setModifier(modifier);
    dropboxFolder.setTitle(title);
    dropboxFolder.setUrlName(urlName);
    dropboxFolder.setPublicity(publicity);
    dropboxFolder.setLanguage(null);
    dropboxFolder.setParentFolder(parentFolder);
    dropboxFolder.setDropboxPath(dropboxPath);
    
    entityManager.persist(dropboxFolder);

    return dropboxFolder;
  }

  public DropboxFolder findByCreatorAndDropboxPath(User creator, String dropboxPath) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DropboxFolder> criteria = criteriaBuilder.createQuery(DropboxFolder.class);
    Root<DropboxFolder> root = criteria.from(DropboxFolder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(DropboxFolder_.dropboxPath), dropboxPath),
        criteriaBuilder.equal(root.get(DropboxFolder_.creator), creator)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
}

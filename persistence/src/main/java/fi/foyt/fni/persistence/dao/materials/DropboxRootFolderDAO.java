package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder;
import fi.foyt.fni.persistence.model.materials.DropboxRootFolder_;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class DropboxRootFolderDAO extends GenericDAO<DropboxRootFolder> {

	private static final long serialVersionUID = 1L;

	public DropboxRootFolder create(User creator, Date created, User modifier, Date modified, Folder parentFolder, String urlName, String title, MaterialPublicity publicity, String deltaCursor, Date lastSynchronized) {
    EntityManager entityManager = getEntityManager();

    DropboxRootFolder dropboxRootFolder = new DropboxRootFolder();
    dropboxRootFolder.setCreated(created);
    dropboxRootFolder.setCreator(creator);
    dropboxRootFolder.setModified(modified);
    dropboxRootFolder.setModifier(modifier);
    dropboxRootFolder.setTitle(title);
    dropboxRootFolder.setUrlName(urlName);
    dropboxRootFolder.setPublicity(publicity);
    dropboxRootFolder.setLanguage(null);
    dropboxRootFolder.setParentFolder(parentFolder);
    dropboxRootFolder.setDeltaCursor(deltaCursor);
    dropboxRootFolder.setLastSynchronized(lastSynchronized);
    
    entityManager.persist(dropboxRootFolder);

    return dropboxRootFolder;
  }

  public DropboxRootFolder findByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DropboxRootFolder> criteria = criteriaBuilder.createQuery(DropboxRootFolder.class);
    Root<DropboxRootFolder> root = criteria.from(DropboxRootFolder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(DropboxRootFolder_.creator), user)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public List<DropboxRootFolder> listAllSortByAscLastSynchronized(Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DropboxRootFolder> criteria = criteriaBuilder.createQuery(DropboxRootFolder.class);
    Root<DropboxRootFolder> root = criteria.from(DropboxRootFolder.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.asc(root.get(DropboxRootFolder_.lastSynchronized)));
    TypedQuery<DropboxRootFolder> query = entityManager.createQuery(criteria);

    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }
  
  public DropboxRootFolder updateDeltaCursor(DropboxRootFolder dropboxRootFolder, String deltaCursor, User modifier) {
    EntityManager entityManager = getEntityManager();

    dropboxRootFolder.setDeltaCursor(deltaCursor);
    dropboxRootFolder.setModifier(modifier);
    
    entityManager.persist(dropboxRootFolder);
    
    return dropboxRootFolder;
  }

  public DropboxRootFolder updateLastSynchronized(DropboxRootFolder dropboxRootFolder, Date lastSynchronized, User modifier) {
    EntityManager entityManager = getEntityManager();

    dropboxRootFolder.setLastSynchronized(lastSynchronized);
    dropboxRootFolder.setModifier(modifier);
    
    entityManager.persist(dropboxRootFolder);
    
    return dropboxRootFolder;
  }
}

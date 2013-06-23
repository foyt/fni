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
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Folder_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class FolderDAO extends GenericDAO<Folder> {

	public Folder create(User creator, Date created, User modifier, Date modified, Language language, Folder parentFolder,  String urlName, String title, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    Folder folder = new Folder();
    folder.setCreated(created);
    folder.setCreator(creator);
    folder.setModified(modified);
    folder.setModifier(modifier);
    folder.setTitle(title);
    folder.setUrlName(urlName);
    folder.setPublicity(publicity);

    if (language != null)
      folder.setLanguage(language);

    if (parentFolder != null)
      folder.setParentFolder(parentFolder);

    entityManager.persist(folder);

    return folder;
  }
  
  public List<Folder> listByParentFolder(Folder parentFolder) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Folder> criteria = criteriaBuilder.createQuery(Folder.class);
    Root<Folder> root = criteria.from(Folder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Folder_.parentFolder), parentFolder)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<Folder> listByModifiedAfter(Date modifiedAfter) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Folder> criteria = criteriaBuilder.createQuery(Folder.class);
    Root<Folder> root = criteria.from(Folder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.greaterThan(root.get(Folder_.modified), modifiedAfter)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Folder updateCreated(Folder folder, Date created) {
    EntityManager entityManager = getEntityManager();

    folder.setCreated(created);
    
    entityManager.persist(folder);
    return folder;
  }
  
  public Folder updateModifier(Folder folder, User modifier) {
    EntityManager entityManager = getEntityManager();
    folder.setModifier(modifier);
    
    entityManager.persist(folder);
    return folder;
  }
  
  public Folder updateModified(Folder folder, Date modified) {
    EntityManager entityManager = getEntityManager();
    folder.setModified(modified);
    
    entityManager.persist(folder);
    return folder;
  }
  
}

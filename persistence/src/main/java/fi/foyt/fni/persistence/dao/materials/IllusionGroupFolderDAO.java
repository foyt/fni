package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionGroupFolderDAO extends GenericDAO<IllusionGroupFolder> {

  private static final long serialVersionUID = -4644199519384824575L;

  public IllusionGroupFolder create(User creator, IllusionFolder parentFolder, String urlName, String title, MaterialPublicity publicity) {
    Date now = new Date();

    IllusionGroupFolder illusionGroupFolder = new IllusionGroupFolder();
    illusionGroupFolder.setCreated(now);
    illusionGroupFolder.setCreator(creator);
    illusionGroupFolder.setLanguage(null);
    illusionGroupFolder.setModified(now);
    illusionGroupFolder.setModifier(creator);
    illusionGroupFolder.setParentFolder(parentFolder);
    illusionGroupFolder.setPublicity(publicity);
    illusionGroupFolder.setTitle(title);
    illusionGroupFolder.setUrlName(urlName);
    
    return persist(illusionGroupFolder);
  }

  public IllusionGroupFolder findByParentFolderAndUrlName(IllusionFolder parentFolder, String urlName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupFolder> criteria = criteriaBuilder.createQuery(IllusionGroupFolder.class);
    Root<IllusionGroupFolder> root = criteria.from(IllusionGroupFolder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupFolder_.parentFolder), parentFolder),
        criteriaBuilder.equal(root.get(IllusionGroupFolder_.urlName), urlName)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
}

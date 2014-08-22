package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionEventFolder;
import fi.foyt.fni.persistence.model.materials.IllusionGroupFolder_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionEventFolderDAO extends GenericDAO<IllusionEventFolder> {

  private static final long serialVersionUID = -4644199519384824575L;

  public IllusionEventFolder create(User creator, IllusionFolder parentFolder, String urlName, String title, MaterialPublicity publicity) {
    Date now = new Date();

    IllusionEventFolder illusionEventFolder = new IllusionEventFolder();
    illusionEventFolder.setCreated(now);
    illusionEventFolder.setCreator(creator);
    illusionEventFolder.setLanguage(null);
    illusionEventFolder.setModified(now);
    illusionEventFolder.setModifier(creator);
    illusionEventFolder.setParentFolder(parentFolder);
    illusionEventFolder.setPublicity(publicity);
    illusionEventFolder.setTitle(title);
    illusionEventFolder.setUrlName(urlName);
    
    return persist(illusionEventFolder);
  }

  public IllusionEventFolder findByParentFolderAndUrlName(IllusionFolder parentFolder, String urlName) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventFolder> criteria = criteriaBuilder.createQuery(IllusionEventFolder.class);
    Root<IllusionEventFolder> root = criteria.from(IllusionEventFolder.class);
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

package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.IllusionFolder;
import fi.foyt.fni.persistence.model.materials.IllusionFolder_;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

public class IllusionFolderDAO extends GenericDAO<IllusionFolder> {

  private static final long serialVersionUID = -4644199519384824575L;

  public IllusionFolder create(User creator, String urlName, String title, MaterialPublicity publicity) {
    Date now = new Date();

    IllusionFolder illusionFolder = new IllusionFolder();
    illusionFolder.setCreated(now);
    illusionFolder.setCreator(creator);
    illusionFolder.setLanguage(null);
    illusionFolder.setModified(now);
    illusionFolder.setModifier(creator);
    illusionFolder.setParentFolder(null);
    illusionFolder.setPublicity(publicity);
    illusionFolder.setTitle(title);
    illusionFolder.setUrlName(urlName);
    
    return persist(illusionFolder);
  }

  public IllusionFolder findByCreator(User creator) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionFolder> criteria = criteriaBuilder.createQuery(IllusionFolder.class);
    Root<IllusionFolder> root = criteria.from(IllusionFolder.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionFolder_.creator), creator)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
}

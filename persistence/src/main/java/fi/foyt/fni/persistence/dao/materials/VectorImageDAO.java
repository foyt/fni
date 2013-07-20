package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.materials.VectorImage;
import fi.foyt.fni.persistence.model.materials.VectorImage_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class VectorImageDAO extends GenericDAO<VectorImage> {

	private static final long serialVersionUID = 1L;

	public VectorImage create(User creator, Language language, Folder parentFolder,  String urlName, String title, String data, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date();

    VectorImage vectorImage = new VectorImage();
    vectorImage.setCreated(now);
    vectorImage.setCreator(creator);
    vectorImage.setData(data);
    vectorImage.setLanguage(language);
    vectorImage.setModified(now);
    vectorImage.setModifier(creator);
    vectorImage.setParentFolder(parentFolder);
    vectorImage.setTitle(title);
    vectorImage.setUrlName(urlName);
    vectorImage.setPublicity(publicity);
    
    entityManager.persist(vectorImage);

    return vectorImage;
  }

  public Number lengthDataByCreator(User creator) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Number> criteria = criteriaBuilder.createQuery(Number.class);
    Root<VectorImage> root = criteria.from(VectorImage.class);
    criteria.select(
      criteriaBuilder.coalesce(
        criteriaBuilder.sum(
          criteriaBuilder.length(root.get(VectorImage_.data))
        ),
        0
      )
    );

    criteria.where(
      criteriaBuilder.equal(root.get(VectorImage_.creator), creator)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public VectorImage updateTitle(VectorImage vectorImage, User modifier, String title) {
    EntityManager entityManager = getEntityManager();

    vectorImage.setTitle(title);
    vectorImage.setModified(new Date());
    vectorImage.setModifier(modifier);
    
    entityManager.persist(vectorImage);
    return vectorImage;
  }

  public VectorImage updateData(VectorImage vectorImage, User modifier, String data) {
    EntityManager entityManager = getEntityManager();

    vectorImage.setData(data);
    vectorImage.setModified(new Date());
    vectorImage.setModifier(modifier);
    
    entityManager.persist(vectorImage);
    return vectorImage;
  }

  public VectorImage updateLanguage(VectorImage vectorImage, User modifier, Language language) {
    EntityManager entityManager = getEntityManager();

    vectorImage.setLanguage(language);
    vectorImage.setModified(new Date());
    vectorImage.setModifier(modifier);
    
    entityManager.persist(vectorImage);
    return vectorImage;
  }
}

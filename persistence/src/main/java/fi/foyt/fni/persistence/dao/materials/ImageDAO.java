package fi.foyt.fni.persistence.dao.materials;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.Image_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class ImageDAO extends GenericDAO<Image> {

	public Image create(User creator, Date created, User modifier, Date modified, Language language, Folder parentFolder, String urlName, String title, byte[] data, String contentType, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    Image image = new Image();
    image.setCreated(created);
    image.setCreator(creator);
    image.setData(data);
    image.setModified(modified);
    image.setModifier(modifier);
    image.setTitle(title);
    image.setUrlName(urlName);
    image.setContentType(contentType);
    image.setPublicity(publicity);

    if (language != null)
      image.setLanguage(language);

    if (parentFolder != null)
      image.setParentFolder(parentFolder);

    entityManager.persist(image);

    return image;
  }
  
  public List<Image> listByModifiedAfter(Date modifiedAfter) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Image> criteria = criteriaBuilder.createQuery(Image.class);
    Root<Image> root = criteria.from(Image.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.greaterThan(root.get(Image_.modified), modifiedAfter)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Image updateTitle(Image image, User modifier, String title) {
    EntityManager entityManager = getEntityManager();

    image.setTitle(title);
    image.setModified(new Date());
    image.setModifier(modifier);
    
    image = entityManager.merge(image);
    return image;
  }

  public Image updateData(Image image, User modifier, String data) {
    EntityManager entityManager = getEntityManager();

    try {
      image.setData(data.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new PersistenceException(e);
    }

    image.setModified(new Date());
    image.setModifier(modifier);

    image = entityManager.merge(image);
    return image;
  }

  public Image updateContentType(Image document, User modifier, String contentType) {
    EntityManager entityManager = getEntityManager();

    document.setContentType(contentType);
    document.setModified(new Date());
    document.setModifier(modifier);
    
    document = entityManager.merge(document);
    return document;
  }

}

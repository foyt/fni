package fi.foyt.fni.persistence.dao.materials;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.materials.Document;
import fi.foyt.fni.persistence.model.materials.Document_;
import fi.foyt.fni.persistence.model.materials.Folder;
import fi.foyt.fni.persistence.model.materials.MaterialPublicity;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class DocumentDAO extends GenericDAO<Document> {

	private static final long serialVersionUID = 1L;

	public Document create(User creator, Language language, Folder parentFolder,  String urlName, String title, String data, MaterialPublicity publicity) {
    EntityManager entityManager = getEntityManager();

    Date now = new Date();

    Document document = new Document();
    try {
      document.setData(data.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new PersistenceException(e);
    }
    document.setCreated(now);
    document.setCreator(creator);
    document.setModified(now);
    document.setModifier(creator);
    document.setTitle(title);
    document.setUrlName(urlName);
    document.setPublicity(publicity);

    if (language != null)
      document.setLanguage(language);

    if (parentFolder != null)
      document.setParentFolder(parentFolder);

    entityManager.persist(document);

    return document;
  }
  
  public List<Document> listByModifiedAfter(Date modifiedAfter) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Document> criteria = criteriaBuilder.createQuery(Document.class);
    Root<Document> root = criteria.from(Document.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.greaterThan(root.get(Document_.modified), modifiedAfter)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public Long lengthDataByCreator(User creator) {
    // Criteria API does not support "length" operation for byte arrays
    // so we use JPQL queries
    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createQuery("select coalesce(sum(length(data)), 0) from Document where creator = :creator");
    query.setParameter("creator", creator);
    return (Long) query.getSingleResult();
  }

  public Document updateTitle(Document document, User modifier, String title) {
    EntityManager entityManager = getEntityManager();

    document.setTitle(title);
    document.setModified(new Date());
    document.setModifier(modifier);
    
    document = entityManager.merge(document);
    return document;
  }

  public Document updateData(Document document, User modifier, String data) {
    EntityManager entityManager = getEntityManager();

    try {
      document.setData(data.getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new PersistenceException(e);
    }
    document.setModified(new Date());
    document.setModifier(modifier);
    
    document = entityManager.merge(document);
    return document;
  }

  public Document updateLanguage(Document document, User modifier, Language language) {
    EntityManager entityManager = getEntityManager();

    document.setLanguage(language);
    document.setModified(new Date());
    document.setModifier(modifier);
    
    document = entityManager.merge(document);
    return document;
  }
}

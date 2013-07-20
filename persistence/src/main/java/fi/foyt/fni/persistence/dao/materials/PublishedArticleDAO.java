package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.PublishedArticle;
import fi.foyt.fni.persistence.model.materials.PublishedArticleType;
import fi.foyt.fni.persistence.model.materials.PublishedArticle_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class PublishedArticleDAO extends GenericDAO<PublishedArticle> {

	private static final long serialVersionUID = 1L;

	public PublishedArticle create(Material material, PublishedArticleType type, User creator, Date created) {
    EntityManager entityManager = getEntityManager();

    PublishedArticle publishedArticle = new PublishedArticle();
    publishedArticle.setMaterial(material);
    publishedArticle.setCreator(creator);
    publishedArticle.setModifier(creator);
    publishedArticle.setCreated(created);
    publishedArticle.setModified(created);
    publishedArticle.setType(type);
    
    entityManager.persist(publishedArticle);

    return publishedArticle;
  }

  public PublishedArticle findByMaterial(Material material) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublishedArticle> criteria = criteriaBuilder.createQuery(PublishedArticle.class);
    Root<PublishedArticle> root = criteria.from(PublishedArticle.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.equal(root.get(PublishedArticle_.material), material)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<PublishedArticle> listByTypeSortByCreated(PublishedArticleType type, Integer firstResult, Integer maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PublishedArticle> criteria = criteriaBuilder.createQuery(PublishedArticle.class);
    Root<PublishedArticle> root = criteria.from(PublishedArticle.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.equal(root.get(PublishedArticle_.type), type)
    );
    
    criteria.orderBy(criteriaBuilder.desc(root.get(PublishedArticle_.created)));
    
    TypedQuery<PublishedArticle> query = entityManager.createQuery(criteria);
    
    if (firstResult != null)
      query.setFirstResult(firstResult);
    
    if (maxResults != null)
      query.setMaxResults(maxResults);

    return query.getResultList();
  }

  public List<PublishedArticle> listAllUserSortByCreated(PublishedArticleType type) {
    return listByTypeSortByCreated(type, null, null);
  }
  
}

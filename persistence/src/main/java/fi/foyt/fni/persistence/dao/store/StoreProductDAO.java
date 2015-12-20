package fi.foyt.fni.persistence.dao.store;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Language;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationImage;
import fi.foyt.fni.persistence.model.gamelibrary.Publication_;
import fi.foyt.fni.persistence.model.store.StoreProduct;
import fi.foyt.fni.persistence.model.users.User;

public class StoreProductDAO extends GenericDAO<StoreProduct> {
  
	private static final long serialVersionUID = 1L;

	public StoreProduct create(String name, String urlName, String description, Double price, Double authorsShare, PublicationImage defaultImage, Date created, User creator, Date modified, User modifier, Boolean published, Integer height, Integer width, Integer depth, Double weight, ForumTopic forumTopic, Language language) {
    
	  StoreProduct storeProduct = new StoreProduct();
    storeProduct.setCreated(created);
    storeProduct.setCreator(creator);
    storeProduct.setDefaultImage(defaultImage);
    storeProduct.setDescription(description);
    storeProduct.setModified(modified);
    storeProduct.setModifier(modifier);
    storeProduct.setName(name);
    storeProduct.setUrlName(urlName);
    storeProduct.setPrice(price);
    storeProduct.setAuthorsShare(authorsShare);
    storeProduct.setPublished(published);
    storeProduct.setHeight(height);
    storeProduct.setWidth(width);
    storeProduct.setDepth(depth);
    storeProduct.setWeight(weight);
    storeProduct.setForumTopic(forumTopic);
    storeProduct.setLanguage(language);
    
    return persist(storeProduct);
	}

  public List<StoreProduct> listByPublished(Boolean published) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreProduct> criteria = criteriaBuilder.createQuery(StoreProduct.class);
    Root<StoreProduct> root = criteria.from(StoreProduct.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Publication_.published), published)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public StoreProduct updateAuthorsShare(StoreProduct bookPublication, Double authorsShare) {
    bookPublication.setAuthorsShare(authorsShare);
    return persist(bookPublication);
  }

}

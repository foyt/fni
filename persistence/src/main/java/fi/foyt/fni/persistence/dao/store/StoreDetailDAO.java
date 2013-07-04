package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.StoreDetail;
import fi.foyt.fni.persistence.model.store.StoreDetail_;

@RequestScoped
@DAO
public class StoreDetailDAO extends GenericDAO<StoreDetail> {
  
	public StoreDetail create(String name) {
		StoreDetail storeDetail = new StoreDetail();
		storeDetail.setName(name);
		getEntityManager().persist(storeDetail);
		return storeDetail;
	}

	public StoreDetail findByName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreDetail> criteria = criteriaBuilder.createQuery(StoreDetail.class);
    Root<StoreDetail> root = criteria.from(StoreDetail.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.equal(root.get(StoreDetail_.name), name)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
}

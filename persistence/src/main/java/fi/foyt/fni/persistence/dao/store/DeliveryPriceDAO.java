package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.DeliveryMethod;
import fi.foyt.fni.persistence.model.store.DeliveryPrice;
import fi.foyt.fni.persistence.model.store.DeliveryPrice_;

@RequestScoped
@DAO
public class DeliveryPriceDAO extends GenericDAO<DeliveryPrice> {

	public DeliveryPrice findByDeliveryMethodAndWeight(DeliveryMethod deliveryMethod, Integer weight) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DeliveryPrice> criteria = criteriaBuilder.createQuery(DeliveryPrice.class);
    Root<DeliveryPrice> root = criteria.from(DeliveryPrice.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(DeliveryPrice_.deliveryMethod), deliveryMethod),
    		criteriaBuilder.ge(root.get(DeliveryPrice_.fromWeight), weight),
    		criteriaBuilder.le(root.get(DeliveryPrice_.toWeight), weight)
    	)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}
	
	
}

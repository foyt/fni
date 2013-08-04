package fi.foyt.fni.persistence.dao.gamelibrary;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.gamelibrary.StoreTag;
import fi.foyt.fni.persistence.model.gamelibrary.StoreTag_;

@DAO
public class StoreTagDAO extends GenericDAO<StoreTag> {
  
	private static final long serialVersionUID = 1L;

	public StoreTag create(String text) {
		StoreTag storeTag = new StoreTag();
		storeTag.setText(text);
		getEntityManager().persist(storeTag);
		return storeTag;
	}

	public StoreTag findByText(String text) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StoreTag> criteria = criteriaBuilder.createQuery(StoreTag.class);
    Root<StoreTag> root = criteria.from(StoreTag.class);
    criteria.select(root);
    criteria.where(
    		criteriaBuilder.equal(root.get(StoreTag_.text), text)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}
	
}

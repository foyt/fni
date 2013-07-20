package fi.foyt.fni.persistence.dao.common;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.common.Country_;

@DAO
public class CountryDAO extends GenericDAO<Country> {

	private static final long serialVersionUID = 1L;

	public Country findByCode(String code) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Country> criteria = criteriaBuilder.createQuery(Country.class);
    Root<Country> root = criteria.from(Country.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(Country_.code), code)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

}

package fi.foyt.fni.persistence.dao.common;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.LocalizedString;
import fi.foyt.fni.persistence.model.common.LocalizedString_;
import fi.foyt.fni.persistence.model.common.MultilingualString;

@RequestScoped
@DAO
public class LocalizedStringDAO extends GenericDAO<LocalizedString> {

	public LocalizedString create(MultilingualString multilingualString, Locale locale, String value) {
    EntityManager entityManager = getEntityManager();

    LocalizedString localizedString = new LocalizedString();
    localizedString.setLocale(locale);
    localizedString.setValue(value);

    entityManager.persist(localizedString);

    multilingualString.addString(localizedString);

    entityManager.persist(multilingualString);

    return localizedString;
  }

	public LocalizedString findByMultilingualStringAndLocale(MultilingualString multilingualString, Locale locale) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<LocalizedString> criteria = criteriaBuilder.createQuery(LocalizedString.class);
    Root<LocalizedString> root = criteria.from(LocalizedString.class);
    criteria.select(root);
    criteria.where(
    	criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(LocalizedString_.multilingualString), multilingualString),
    		criteriaBuilder.equal(root.get(LocalizedString_.locale), locale)
    	)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}
	
	public LocalizedString updateValue(LocalizedString localizedString, String value) {
		localizedString.setValue(value);
		
		getEntityManager().persist(localizedString);
		
		return localizedString;
	}

}

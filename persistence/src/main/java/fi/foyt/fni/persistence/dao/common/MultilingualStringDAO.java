package fi.foyt.fni.persistence.dao.common;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.LocalizedString;
import fi.foyt.fni.persistence.model.common.MultilingualString;

@RequestScoped
@DAO
public class MultilingualStringDAO extends GenericDAO<MultilingualString> {

	public MultilingualString create() {
    EntityManager entityManager = getEntityManager();

    MultilingualString multilingualString = new MultilingualString();
    entityManager.persist(multilingualString);

    return multilingualString;
  }

	public MultilingualString updateDefaultString(MultilingualString multilingualString, LocalizedString defaultString) {
		multilingualString.setDefaultString(defaultString);
		
		getEntityManager().persist(multilingualString);
		
		return multilingualString;
	}
	
}

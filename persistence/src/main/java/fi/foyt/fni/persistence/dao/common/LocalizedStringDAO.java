package fi.foyt.fni.persistence.dao.common;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.LocalizedString;
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

}

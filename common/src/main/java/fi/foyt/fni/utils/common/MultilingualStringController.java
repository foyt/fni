package fi.foyt.fni.utils.common;

import java.util.Locale;

import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.common.LocalizedStringDAO;
import fi.foyt.fni.persistence.model.common.LocalizedString;
import fi.foyt.fni.persistence.model.common.MultilingualString;

@Stateless
@Dependent
public class MultilingualStringController {
	
	@DAO
	@Inject
	private LocalizedStringDAO localizedStringDAO;

	public String getLocalizedString(Locale locale, MultilingualString multilingualString) {
		LocalizedString localizedString = localizedStringDAO.findByMultilingualStringAndLocale(multilingualString, locale);
		return localizedString != null ? localizedString.getValue() : null;
	}
	
}

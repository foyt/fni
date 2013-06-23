package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.MultilingualString;
import fi.foyt.fni.persistence.model.store.StoreTag;

@RequestScoped
@DAO
public class StoreTagDAO extends GenericDAO<StoreTag> {
  
	public StoreTag create(MultilingualString text) {
		StoreTag storeTag = new StoreTag();
		storeTag.setText(text);
		getEntityManager().persist(storeTag);
		return storeTag;
	}
  
}

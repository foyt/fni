package fi.foyt.fni.persistence.dao.store;

import javax.enterprise.context.RequestScoped;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.store.DeliveryMethod;

@RequestScoped
@DAO
public class DeliveryMethodDAO extends GenericDAO<DeliveryMethod> {
  
}

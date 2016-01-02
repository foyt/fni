package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm_;

public class IllusionEventRegistrationFormDAO extends GenericDAO<IllusionEventRegistrationForm> {

	private static final long serialVersionUID = 1L;

	public IllusionEventRegistrationForm create(IllusionEvent event, String data) {
	  IllusionEventRegistrationForm illusionEventRegistrationForm = new IllusionEventRegistrationForm();
    
	  illusionEventRegistrationForm.setEvent(event);
	  illusionEventRegistrationForm.setData(data);
	  
		return persist(illusionEventRegistrationForm);
	}

  public List<IllusionEventRegistrationForm> listByEvent(IllusionEvent event) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventRegistrationForm> criteria = criteriaBuilder.createQuery(IllusionEventRegistrationForm.class);
    Root<IllusionEventRegistrationForm> root = criteria.from(IllusionEventRegistrationForm.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventRegistrationForm_.event), event)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public IllusionEventRegistrationForm updateData(IllusionEventRegistrationForm form, String data) {
    form.setData(data);
    return persist(form);
  }

}

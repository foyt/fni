package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationForm;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormField;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormField_;

public class IllusionEventRegistrationFormFieldDAO extends GenericDAO<IllusionEventRegistrationFormField> {

	private static final long serialVersionUID = 1L;

	public IllusionEventRegistrationFormField create(IllusionEventRegistrationForm form, String name) {
	  IllusionEventRegistrationFormField field = new IllusionEventRegistrationFormField();
    
	  field.setForm(form);
	  field.setName(name);
	  
		return persist(field);
	}

  public IllusionEventRegistrationFormField findByFormAndName(IllusionEventRegistrationForm form, String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventRegistrationFormField> criteria = criteriaBuilder.createQuery(IllusionEventRegistrationFormField.class);
    Root<IllusionEventRegistrationFormField> root = criteria.from(IllusionEventRegistrationFormField.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventRegistrationFormField_.form), form),
        criteriaBuilder.equal(root.get(IllusionEventRegistrationFormField_.name), name)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventRegistrationFormField> listByForm(IllusionEventRegistrationForm form) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventRegistrationFormField> criteria = criteriaBuilder.createQuery(IllusionEventRegistrationFormField.class);
    Root<IllusionEventRegistrationFormField> root = criteria.from(IllusionEventRegistrationFormField.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventRegistrationFormField_.form), form)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

}

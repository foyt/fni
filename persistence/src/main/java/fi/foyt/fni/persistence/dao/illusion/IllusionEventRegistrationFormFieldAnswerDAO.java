package fi.foyt.fni.persistence.dao.illusion;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormField;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormFieldAnswer;
import fi.foyt.fni.persistence.model.illusion.IllusionEventRegistrationFormFieldAnswer_;

public class IllusionEventRegistrationFormFieldAnswerDAO extends GenericDAO<IllusionEventRegistrationFormFieldAnswer> {

	private static final long serialVersionUID = 1L;

	public IllusionEventRegistrationFormFieldAnswer create(IllusionEventRegistrationFormField field, IllusionEventParticipant participant, String value) {
	  IllusionEventRegistrationFormFieldAnswer answer = new IllusionEventRegistrationFormFieldAnswer();
    
	  answer.setField(field);
	  answer.setParticipant(participant);
	  answer.setValue(value);
	  
		return persist(answer);
	}

  public IllusionEventRegistrationFormFieldAnswer findByFieldAndParticipant(IllusionEventRegistrationFormField field, IllusionEventParticipant participant) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventRegistrationFormFieldAnswer> criteria = criteriaBuilder.createQuery(IllusionEventRegistrationFormFieldAnswer.class);
    Root<IllusionEventRegistrationFormFieldAnswer> root = criteria.from(IllusionEventRegistrationFormFieldAnswer.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventRegistrationFormFieldAnswer_.field), field),
        criteriaBuilder.equal(root.get(IllusionEventRegistrationFormFieldAnswer_.participant), participant)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }
  
  public IllusionEventRegistrationFormFieldAnswer updateValue(IllusionEventRegistrationFormFieldAnswer answer, String value) {
    answer.setValue(value);
    return persist(answer);
  }

}

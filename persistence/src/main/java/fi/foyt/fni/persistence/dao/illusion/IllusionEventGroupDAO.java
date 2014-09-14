package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup_;

public class IllusionEventGroupDAO extends GenericDAO<IllusionEventGroup> {

	private static final long serialVersionUID = 1L;

	public IllusionEventGroup create(IllusionEvent event, String name) {
	  IllusionEventGroup illusionEventParticipantGroup  = new IllusionEventGroup();

    illusionEventParticipantGroup.setEvent(event);
    illusionEventParticipantGroup.setName(name);
    
		return persist(illusionEventParticipantGroup);
	}

  public List<IllusionEventGroup> listByEvent(IllusionEvent event) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventGroup> criteria = criteriaBuilder.createQuery(IllusionEventGroup.class);
    Root<IllusionEventGroup> root = criteria.from(IllusionEventGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventGroup_.event), event)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public IllusionEventGroup updateName(IllusionEventGroup group, String name) {
    group.setName(name);
    return persist(group);
  }
  
}

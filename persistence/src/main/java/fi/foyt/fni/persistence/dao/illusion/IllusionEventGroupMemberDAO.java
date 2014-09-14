package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGroupMember_;

public class IllusionEventGroupMemberDAO extends GenericDAO<IllusionEventGroupMember> {

	private static final long serialVersionUID = 1L;

	public IllusionEventGroupMember create(IllusionEventGroup group, IllusionEventParticipant participant) {
	  IllusionEventGroupMember illusionEventParticipantGroupMember = new IllusionEventGroupMember();

    illusionEventParticipantGroupMember.setGroup(group);
    illusionEventParticipantGroupMember.setParticipant(participant);

		return persist(illusionEventParticipantGroupMember);
	}

  public IllusionEventGroupMember findByGroupAndParticipant(IllusionEventGroup group, IllusionEventParticipant participant) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventGroupMember> criteria = criteriaBuilder.createQuery(IllusionEventGroupMember.class);
    Root<IllusionEventGroupMember> root = criteria.from(IllusionEventGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(IllusionEventGroupMember_.participant), participant),
          criteriaBuilder.equal(root.get(IllusionEventGroupMember_.group), group)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventGroupMember> listByGroup(IllusionEventGroup group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventGroupMember> criteria = criteriaBuilder.createQuery(IllusionEventGroupMember.class);
    Root<IllusionEventGroupMember> root = criteria.from(IllusionEventGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventGroupMember_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

}

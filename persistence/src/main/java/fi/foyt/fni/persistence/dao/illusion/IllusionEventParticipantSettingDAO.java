package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberSetting_;
import fi.foyt.fni.persistence.model.illusion.IllusionEventSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantSetting;

public class IllusionEventParticipantSettingDAO extends GenericDAO<IllusionEventParticipantSetting> {

	private static final long serialVersionUID = 1L;

	public IllusionEventParticipantSetting create(IllusionEventParticipant member, IllusionEventSettingKey key, String value) {
	  IllusionEventParticipantSetting illusionGroupUserSetting = new IllusionEventParticipantSetting();

    illusionGroupUserSetting.setKey(key);
    illusionGroupUserSetting.setMember(member);
    illusionGroupUserSetting.setValue(value);

		return persist(illusionGroupUserSetting);
	}

  public IllusionEventParticipantSetting findByMemberAndKey(IllusionEventParticipant member, IllusionEventSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipantSetting> criteria = criteriaBuilder.createQuery(IllusionEventParticipantSetting.class);
    Root<IllusionEventParticipantSetting> root = criteria.from(IllusionEventParticipantSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMemberSetting_.member), member),
        criteriaBuilder.equal(root.get(IllusionGroupMemberSetting_.key), key)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventParticipantSetting> listByMember(IllusionEventParticipant member) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventParticipantSetting> criteria = criteriaBuilder.createQuery(IllusionEventParticipantSetting.class);
    Root<IllusionEventParticipantSetting> root = criteria.from(IllusionEventParticipantSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionGroupMemberSetting_.member), member)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionEventParticipantSetting updateValue(IllusionEventParticipantSetting setting, String value) {
	  setting.setValue(value);
		return persist(setting);
	}

}

package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionEventMaterialParticipantSetting_;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.materials.Material;

public class IllusionEventMaterialParticipantSettingDAO extends GenericDAO<IllusionEventMaterialParticipantSetting> {

	private static final long serialVersionUID = 1L;

	public IllusionEventMaterialParticipantSetting create(Material material, IllusionEventParticipant participant, IllusionEventMaterialParticipantSettingKey key, String value) {
	  IllusionEventMaterialParticipantSetting illusionEventMaterialParticipantSetting = new IllusionEventMaterialParticipantSetting();

    illusionEventMaterialParticipantSetting.setKey(key);
    illusionEventMaterialParticipantSetting.setMaterial(material);
    illusionEventMaterialParticipantSetting.setParticipant(participant);
    illusionEventMaterialParticipantSetting.setValue(value);
    
		return persist(illusionEventMaterialParticipantSetting);
	}

  public IllusionEventMaterialParticipantSetting findByMaterialAndParticipantAndKey(Material material, IllusionEventParticipant participant, IllusionEventMaterialParticipantSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventMaterialParticipantSetting> criteria = criteriaBuilder.createQuery(IllusionEventMaterialParticipantSetting.class);
    Root<IllusionEventMaterialParticipantSetting> root = criteria.from(IllusionEventMaterialParticipantSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventMaterialParticipantSetting_.material), material),
        criteriaBuilder.equal(root.get(IllusionEventMaterialParticipantSetting_.participant), participant),
        criteriaBuilder.equal(root.get(IllusionEventMaterialParticipantSetting_.key), key)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventMaterialParticipantSetting> listByParticipant(IllusionEventParticipant participant) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventMaterialParticipantSetting> criteria = criteriaBuilder.createQuery(IllusionEventMaterialParticipantSetting.class);
    Root<IllusionEventMaterialParticipantSetting> root = criteria.from(IllusionEventMaterialParticipantSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventMaterialParticipantSetting_.participant), participant)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<IllusionEventMaterialParticipantSetting> listByMaterialAndParticipantAndKey(Material material, IllusionEventParticipant participant) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventMaterialParticipantSetting> criteria = criteriaBuilder.createQuery(IllusionEventMaterialParticipantSetting.class);
    Root<IllusionEventMaterialParticipantSetting> root = criteria.from(IllusionEventMaterialParticipantSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventMaterialParticipantSetting_.material), material),
        criteriaBuilder.equal(root.get(IllusionEventMaterialParticipantSetting_.participant), participant)
      )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionEventMaterialParticipantSetting updateValue(IllusionEventMaterialParticipantSetting illusionEventMaterialParticipantSetting, String value) {
	  illusionEventMaterialParticipantSetting.setValue(value);
		return persist(illusionEventMaterialParticipantSetting);
	}

}

package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberSetting_;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupSettingKey;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberSetting;

public class IllusionGroupMemberSettingDAO extends GenericDAO<IllusionGroupMemberSetting> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupMemberSetting create(IllusionGroupMember member, IllusionGroupSettingKey key, String value) {
	  IllusionGroupMemberSetting illusionGroupUserSetting = new IllusionGroupMemberSetting();

    illusionGroupUserSetting.setKey(key);
    illusionGroupUserSetting.setMember(member);
    illusionGroupUserSetting.setValue(value);

		return persist(illusionGroupUserSetting);
	}

  public IllusionGroupMemberSetting findByMemberAndKey(IllusionGroupMember member, IllusionGroupSettingKey key) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMemberSetting> criteria = criteriaBuilder.createQuery(IllusionGroupMemberSetting.class);
    Root<IllusionGroupMemberSetting> root = criteria.from(IllusionGroupMemberSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionGroupMemberSetting_.member), member),
        criteriaBuilder.equal(root.get(IllusionGroupMemberSetting_.key), key)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionGroupMemberSetting> listByMember(IllusionGroupMember member) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMemberSetting> criteria = criteriaBuilder.createQuery(IllusionGroupMemberSetting.class);
    Root<IllusionGroupMemberSetting> root = criteria.from(IllusionGroupMemberSetting.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionGroupMemberSetting_.member), member)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionGroupMemberSetting updateValue(IllusionGroupMemberSetting setting, String value) {
	  setting.setValue(value);
		return persist(setting);
	}

}

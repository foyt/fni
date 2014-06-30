package fi.foyt.fni.persistence.dao.illusion;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup_;

public class IllusionGroupDAO extends GenericDAO<IllusionGroup> {

	private static final long serialVersionUID = 1L;

	public IllusionGroup create(String urlName, String name, String description, String indexText, String xmppRoom, Date created) {
		IllusionGroup illusionGroup = new IllusionGroup();
		
		illusionGroup.setName(name);
		illusionGroup.setDescription(description);
		illusionGroup.setUrlName(urlName);
		illusionGroup.setXmppRoom(xmppRoom);
		illusionGroup.setCreated(created);
		illusionGroup.setIndexText(indexText);

		return persist(illusionGroup);
	}

	public IllusionGroup findByUrlName(String urlName) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroup> criteria = criteriaBuilder.createQuery(IllusionGroup.class);
    Root<IllusionGroup> root = criteria.from(IllusionGroup.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(IllusionGroup_.urlName), urlName)
    );

    return getSingleResult(entityManager.createQuery(criteria));
	}

  public IllusionGroup updateName(IllusionGroup illusionGroup, String name) {
    illusionGroup.setName(name);
    return persist(illusionGroup);
  }

  public IllusionGroup updateIndexText(IllusionGroup illusionGroup, String indexText) {
    illusionGroup.setIndexText(indexText);
    return persist(illusionGroup);
  }
	
}

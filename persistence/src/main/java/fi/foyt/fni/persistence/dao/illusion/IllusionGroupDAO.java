package fi.foyt.fni.persistence.dao.illusion;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup;
import fi.foyt.fni.persistence.model.illusion.IllusionGroup_;;

@DAO
public class IllusionGroupDAO extends GenericDAO<IllusionGroup> {

	private static final long serialVersionUID = 1L;

	public IllusionGroup create(String urlName, String name, String description, String xmppRoom) {
		IllusionGroup illusionSpace = new IllusionGroup();
		
		illusionSpace.setName(name);
		illusionSpace.setDescription(description);
		illusionSpace.setUrlName(urlName);
		illusionSpace.setXmppRoom(xmppRoom);
		
		getEntityManager().persist(illusionSpace);
		
		return illusionSpace;
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

	public IllusionGroup updateName(IllusionGroup illusionSpace, String name) {
		illusionSpace.setName(name);
		return persist(illusionSpace);
	}
	
}

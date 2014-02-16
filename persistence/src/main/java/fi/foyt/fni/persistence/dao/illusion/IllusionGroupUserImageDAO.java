package fi.foyt.fni.persistence.dao.illusion;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUser;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserImage;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupUserImage_;

@DAO
public class IllusionGroupUserImageDAO extends GenericDAO<IllusionGroupUserImage> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupUserImage create(IllusionGroupUser user, String contentType, byte[] data, Date modified) {
		IllusionGroupUserImage illusionGroupUserImage = new IllusionGroupUserImage();

    illusionGroupUserImage.setUser(user);
    illusionGroupUserImage.setData(data);
    illusionGroupUserImage.setContentType(contentType);
    illusionGroupUserImage.setModified(modified);
    
		return persist(illusionGroupUserImage);
	}

  public IllusionGroupUserImage findByUser(IllusionGroupUser user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupUserImage> criteria = criteriaBuilder.createQuery(IllusionGroupUserImage.class);
    Root<IllusionGroupUserImage> root = criteria.from(IllusionGroupUserImage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionGroupUserImage_.user), user)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

}

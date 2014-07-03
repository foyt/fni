package fi.foyt.fni.persistence.dao.illusion;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMember;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberImage;
import fi.foyt.fni.persistence.model.illusion.IllusionGroupMemberImage_;

public class IllusionGroupMemberImageDAO extends GenericDAO<IllusionGroupMemberImage> {

	private static final long serialVersionUID = 1L;

	public IllusionGroupMemberImage create(IllusionGroupMember member, String contentType, byte[] data, Date modified) {
		IllusionGroupMemberImage illusionGroupUserImage = new IllusionGroupMemberImage();

    illusionGroupUserImage.setMember(member);
    illusionGroupUserImage.setData(data);
    illusionGroupUserImage.setContentType(contentType);
    illusionGroupUserImage.setModified(modified);
    
		return persist(illusionGroupUserImage);
	}

  public IllusionGroupMemberImage findByMember(IllusionGroupMember member) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionGroupMemberImage> criteria = criteriaBuilder.createQuery(IllusionGroupMemberImage.class);
    Root<IllusionGroupMemberImage> root = criteria.from(IllusionGroupMemberImage.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionGroupMemberImage_.member), member)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public IllusionGroupMemberImage updateData(IllusionGroupMemberImage image, byte[] data) {
    image.setData(data);
    return persist(image);
  }

  public IllusionGroupMemberImage updateContentType(IllusionGroupMemberImage image, String contentType) {
    image.setContentType(contentType);
    return persist(image);
  }

  public IllusionGroupMemberImage updateModified(IllusionGroupMemberImage image, Date modified) {
    image.setModified(modified);
    return persist(image);
  }

}

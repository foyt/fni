package fi.foyt.fni.persistence.dao.users;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.users.UserImage_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserImage;

@DAO
public class UserImageDAO extends GenericDAO<UserImage> {

	private static final long serialVersionUID = 1L;

	public UserImage create(User user, String contentType, byte[] data, Date modified) {
    UserImage userImage = new UserImage();
    userImage.setUser(user);
    userImage.setContentType(contentType);
    userImage.setData(data);
    userImage.setModified(modified);

    return persist(userImage);
  }

  public UserImage findByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserImage> criteria = criteriaBuilder.createQuery(UserImage.class);
    Root<UserImage> root = criteria.from(UserImage.class);
    criteria.select(root);
    criteria.where(
  		criteriaBuilder.equal(root.get(UserImage_.user), user)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

	public UserImage updateData(UserImage userImage, byte[] data, Date modified) {
		userImage.setData(data);
		userImage.setModified(modified);
		return persist(userImage);
	}

	public UserImage updateContentType(UserImage userImage, String contentType, Date modified) {
		userImage.setContentType(contentType);
		userImage.setModified(modified);
		return persist(userImage);
	}
  
}

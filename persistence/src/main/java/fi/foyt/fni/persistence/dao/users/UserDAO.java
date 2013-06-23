package fi.foyt.fni.persistence.dao.users;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.persistence.model.users.User_;

@RequestScoped
@DAO
public class UserDAO extends GenericDAO<User> {

	public User create(String firstName, String lastName, String nickname, Locale locale, Date registrationDate, UserRole role) {
    EntityManager entityManager = getEntityManager();

    User user = new User();
    user.setArchived(Boolean.FALSE);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setLocale(locale.toString());
    user.setNickname(nickname);
    user.setPremiumExpires(null);
    user.setRole(role);
    user.setRegistrationDate(registrationDate);

    entityManager.persist(user);

    return user;
  }
  
  public List <User> listByProfileImage(Image image) {
  	EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
    Root<User> root = criteria.from(User.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(User_.profileImage), image));

    return entityManager.createQuery(criteria).getResultList();
  }

  public User updateFirstName(User user, String firstName) {
    EntityManager entityManager = getEntityManager();

    user.setFirstName(firstName);

    entityManager.persist(user);
    
    return user;
  }

  public User updateLastName(User user, String lastName) {
    EntityManager entityManager = getEntityManager();
    user.setLastName(lastName);

    entityManager.persist(user);
    
    return user;
  }

  public User updateNickname(User user, String nickname) {
    EntityManager entityManager = getEntityManager();

    user.setNickname(nickname);

    entityManager.persist(user);
    
    return user;

  }

  public User updateLocale(User user, Locale locale) {
    EntityManager entityManager = getEntityManager();
    user.setLocale(locale.toString());

    entityManager.persist(user);
    
    return user;

  }

  public User updateRegistrationDate(User user, Date registrationDate) {
    EntityManager entityManager = getEntityManager();
    user.setRegistrationDate(registrationDate);

    entityManager.persist(user);
    
    return user;

  }

  public User updateArchived(User user, Boolean archived) {
    EntityManager entityManager = getEntityManager();

    user.setArchived(archived);

    entityManager.persist(user);
    
    return user;

  }

  public User updateRole(User user, UserRole role) {
    EntityManager entityManager = getEntityManager();

    user.setRole(role);

    entityManager.persist(user);
    
    return user;
  }

	public User updateProfileImage(User user, Image profileImage) {
		EntityManager entityManager = getEntityManager();

    user.setProfileImage(profileImage);

    entityManager.persist(user);
    
    return user;
  }

}

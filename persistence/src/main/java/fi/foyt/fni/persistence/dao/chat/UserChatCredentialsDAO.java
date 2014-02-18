package fi.foyt.fni.persistence.dao.chat;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.chat.UserChatCredentials_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class UserChatCredentialsDAO extends GenericDAO<UserChatCredentials> {

	private static final long serialVersionUID = 1L;

	public UserChatCredentials create(User user, String userJid, String password) {
    UserChatCredentials userChatCredentials = new UserChatCredentials();
    userChatCredentials.setUserJid(userJid);
    userChatCredentials.setPassword(password);
    userChatCredentials.setUser(user);

    return persist(userChatCredentials);
  }

  public UserChatCredentials findByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserChatCredentials> criteria = criteriaBuilder.createQuery(UserChatCredentials.class);
    Root<UserChatCredentials> root = criteria.from(UserChatCredentials.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserChatCredentials_.user), user));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public UserChatCredentials findByUserJid(String userJid) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserChatCredentials> criteria = criteriaBuilder.createQuery(UserChatCredentials.class);
    Root<UserChatCredentials> root = criteria.from(UserChatCredentials.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserChatCredentials_.userJid), userJid));
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public UserChatCredentials updateUserJid(UserChatCredentials userChatCredentials, String userJid) {
    userChatCredentials.setUserJid(userJid);
    return persist(userChatCredentials);
  }

  public UserChatCredentials updatePassword(UserChatCredentials userChatCredentials, String password) {
    userChatCredentials.setPassword(password);
    return persist(userChatCredentials);
  }

}

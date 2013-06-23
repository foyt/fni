package fi.foyt.fni.persistence.dao.chat;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.chat.UserChatCredentials_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.chat.UserChatCredentials;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class UserChatCredentialsDAO extends GenericDAO<UserChatCredentials> {

	public UserChatCredentials create(User user, String userJid, String password) {
    EntityManager entityManager = getEntityManager();

    UserChatCredentials userChatToken = new UserChatCredentials();
    userChatToken.setUserJid(userJid);
    userChatToken.setPassword(password);
    userChatToken.setUser(user);
    entityManager.persist(userChatToken);
    
    return userChatToken;
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

  public UserChatCredentials updateUserJid(UserChatCredentials userChatToken, String userJid) {
    userChatToken.setUserJid(userJid);
    
    getEntityManager().persist(userChatToken);
    
    return userChatToken;
  }

  public UserChatCredentials updatePassword(UserChatCredentials userChatToken, String password) {
    userChatToken.setPassword(password);
    
    getEntityManager().persist(userChatToken);
    
    return userChatToken;
  }
}

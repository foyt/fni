package fi.foyt.fni.persistence.dao.users;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.CommonFriend;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserFriend;
import fi.foyt.fni.persistence.model.users.UserFriend_;
import fi.foyt.fni.persistence.model.users.User_;

@DAO
public class UserFriendDAO extends GenericDAO<UserFriend> {

	private static final long serialVersionUID = 1L;

	public UserFriend create(User user, User friend, Boolean confirmed) {
    EntityManager entityManager = getEntityManager();
    
    UserFriend userFriend = new UserFriend();
    userFriend.setFriend(friend);
    userFriend.setUser(user);
    userFriend.setConfirmed(confirmed);
    
    entityManager.persist(userFriend);
    
    return userFriend;
  }

  public UserFriend findByUserAndFriend(User user, User friend) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserFriend> criteria = criteriaBuilder.createQuery(UserFriend.class);
    Root<UserFriend> root = criteria.from(UserFriend.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(UserFriend_.user), user),
            criteriaBuilder.equal(root.get(UserFriend_.friend), friend)
        )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<UserFriend> listByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserFriend> criteria = criteriaBuilder.createQuery(UserFriend.class);
    Root<UserFriend> root = criteria.from(UserFriend.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserFriend_.user), user));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<UserFriend> listByUserAndConfirmed(User user, Boolean confirmed) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserFriend> criteria = criteriaBuilder.createQuery(UserFriend.class);
    Root<UserFriend> root = criteria.from(UserFriend.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(UserFriend_.user), user),
            criteriaBuilder.equal(root.get(UserFriend_.confirmed), confirmed)
        )
    );

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<UserFriend> listByFriend(User friend) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserFriend> criteria = criteriaBuilder.createQuery(UserFriend.class);
    Root<UserFriend> root = criteria.from(UserFriend.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(UserFriend_.friend), friend));

    return entityManager.createQuery(criteria).getResultList();
  }

  public List<UserFriend> listByFriendAndConfirmed(User friend, Boolean confirmed) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserFriend> criteria = criteriaBuilder.createQuery(UserFriend.class);
    Root<UserFriend> root = criteria.from(UserFriend.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
            criteriaBuilder.equal(root.get(UserFriend_.friend), friend),
            criteriaBuilder.equal(root.get(UserFriend_.confirmed), confirmed)
        )
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
  public List<User> listFriendUsersByConfirmed(User user, Boolean confirmed) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder friendUsersCriteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> friendUsersCriteria = friendUsersCriteriaBuilder.createQuery(Long.class);
    Root<UserFriend> friendUsersRoot = friendUsersCriteria.from(UserFriend.class);
    Join<UserFriend, User> friendUsersUserJoin = friendUsersRoot.join(UserFriend_.user);
    friendUsersCriteria.select(friendUsersUserJoin.get(User_.id));
    friendUsersCriteria.where(
        friendUsersCriteriaBuilder.and(
            friendUsersCriteriaBuilder.equal(friendUsersRoot.get(UserFriend_.friend), user),
            friendUsersCriteriaBuilder.equal(friendUsersRoot.get(UserFriend_.confirmed), confirmed)
        )
    );
    
    CriteriaBuilder userFriendsCriteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> userFriendsCriteria = userFriendsCriteriaBuilder.createQuery(Long.class);
    Root<UserFriend> userFriendsRoot = userFriendsCriteria.from(UserFriend.class);
    Join<UserFriend, User> usersFriendsUserJoin = userFriendsRoot.join(UserFriend_.friend);
    userFriendsCriteria.select(usersFriendsUserJoin.get(User_.id));
    userFriendsCriteria.where(
        userFriendsCriteriaBuilder.and(
            userFriendsCriteriaBuilder.equal(userFriendsRoot.get(UserFriend_.user), user),
            userFriendsCriteriaBuilder.equal(userFriendsRoot.get(UserFriend_.confirmed), confirmed)
        )
    );
    
    List<Long> friendIds = entityManager.createQuery(userFriendsCriteria).getResultList();
    friendIds.addAll(entityManager.createQuery(friendUsersCriteria).getResultList());
    
    if (friendIds.size() > 0) {
      CriteriaBuilder userCriteriaBuilder = entityManager.getCriteriaBuilder();
      CriteriaQuery<User> userCriteria = userCriteriaBuilder.createQuery(User.class);
      Root<User> userRoot = userCriteria.from(User.class);
      userCriteria.select(userRoot);
      userCriteria.where(
      	userRoot.get(User_.id).in(friendIds)
      );
      return entityManager.createQuery(userCriteria).getResultList();
    } else {
    	return new ArrayList<User>();
    }
  }
  
  public List<CommonFriend> listCommonFriendsByUserOrderByCommonFriendCount(User user) {
  	return listCommonFriendsByUserOrderByCommonFriendCount(user, null, null);
  }
  
  public List<CommonFriend> listCommonFriendsByUserOrderByCommonFriendCount(User user, Integer firstResult, Integer maxResults) {
  	EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tuple> criteria = criteriaBuilder.createTupleQuery();
    Root<UserFriend> root = criteria.from(UserFriend.class);
    
    Path<User> friendPath = root.get(UserFriend_.friend);
    Expression<Long> countExpression = criteriaBuilder.count(friendPath);
    
    Subquery<User> friendsSubquery = criteria.subquery(User.class);
    Root<UserFriend> friendsRoot = friendsSubquery.from(UserFriend.class);
    friendsSubquery.where(
    	criteriaBuilder.and(
        criteriaBuilder.equal(friendsRoot.get(UserFriend_.user), user),
        criteriaBuilder.equal(root.get(UserFriend_.confirmed), Boolean.TRUE)
      )
    );
    friendsSubquery.select(friendsRoot.get(UserFriend_.friend));    
    
    criteria.multiselect(friendPath, countExpression);
    
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.notEqual(root.get(UserFriend_.friend), user),
          root.get(UserFriend_.user).in(friendsSubquery),
          criteriaBuilder.not(
            root.get(UserFriend_.friend).in(friendsSubquery)
          ),
          criteriaBuilder.equal(root.get(UserFriend_.confirmed), Boolean.TRUE)
      )
    );
    
    criteria.groupBy(friendPath);
    criteria.orderBy(criteriaBuilder.desc(countExpression));
    
    List<CommonFriend> commonFriends = new ArrayList<CommonFriend>();

    TypedQuery<Tuple> query = entityManager.createQuery( criteria );
    
    if (firstResult != null)
      query.setFirstResult(firstResult);
    
    if (maxResults != null)
      query.setMaxResults(maxResults);
    
    List<Tuple> tuples = query.getResultList();
    for ( Tuple tuple : tuples ) {
    	commonFriends.add(new CommonFriend(tuple.get(countExpression), tuple.get(friendPath)));
    } 
    
    return commonFriends;
  }
  
  public UserFriend updateConfirmed(UserFriend userFriend, Boolean confirmed) {
    EntityManager entityManager = getEntityManager();
    userFriend.setConfirmed(confirmed);
    entityManager.persist(userFriend);
    return userFriend;
  }
}

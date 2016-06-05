package fi.foyt.fni.persistence.dao.users;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.users.UserGroupMember;
import fi.foyt.fni.persistence.model.users.UserGroupMember_;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserGroup;

public class UserGroupMemberDAO extends GenericDAO<UserGroupMember> {

	private static final long serialVersionUID = 1L;

	public UserGroupMember create(UserGroup group, User user) {
	  UserGroupMember illusionEventParticipantGroupMember = new UserGroupMember();

    illusionEventParticipantGroupMember.setGroup(group);
    illusionEventParticipantGroupMember.setUser(user);

		return persist(illusionEventParticipantGroupMember);
	}

  public UserGroupMember findByGroupAndUser(UserGroup group, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserGroupMember> criteria = criteriaBuilder.createQuery(UserGroupMember.class);
    Root<UserGroupMember> root = criteria.from(UserGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
          criteriaBuilder.equal(root.get(UserGroupMember_.user), user),
          criteriaBuilder.equal(root.get(UserGroupMember_.group), group)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<UserGroupMember> listByGroup(UserGroup group) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserGroupMember> criteria = criteriaBuilder.createQuery(UserGroupMember.class);
    Root<UserGroupMember> root = criteria.from(UserGroupMember.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(UserGroupMember_.group), group)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

}

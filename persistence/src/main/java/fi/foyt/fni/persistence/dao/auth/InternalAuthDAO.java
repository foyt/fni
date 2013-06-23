package fi.foyt.fni.persistence.dao.auth;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.auth.InternalAuth;
import fi.foyt.fni.persistence.model.auth.InternalAuth_;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class InternalAuthDAO extends GenericDAO<InternalAuth> {

	public InternalAuth create(User user, String password, Boolean verified) {
    EntityManager entityManager = getEntityManager();

    InternalAuth internalAuth = new InternalAuth();
    internalAuth.setPassword(password);
    internalAuth.setUser(user);
    internalAuth.setVerified(verified);
    
    entityManager.persist(internalAuth);
    return internalAuth;
  }

  public InternalAuth findByUserAndPassword(User user, String password) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<InternalAuth> criteria = criteriaBuilder.createQuery(InternalAuth.class);
    Root<InternalAuth> root = criteria.from(InternalAuth.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(InternalAuth_.user), user),
          criteriaBuilder.equal(root.get(InternalAuth_.password), password)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public InternalAuth findByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<InternalAuth> criteria = criteriaBuilder.createQuery(InternalAuth.class);
    Root<InternalAuth> root = criteria.from(InternalAuth.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(InternalAuth_.user), user)
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

	public InternalAuth updatePassword(InternalAuth internalAuth, String password) {
		EntityManager entityManager = getEntityManager();
		internalAuth.setPassword(password);
		entityManager.persist(internalAuth);
		return internalAuth;
  }

  public InternalAuth updateVerified(InternalAuth internalAuth, Boolean verified) {
    EntityManager entityManager = getEntityManager();

    internalAuth.setVerified(verified);

    entityManager.persist(internalAuth);
    
    return internalAuth;

  }
}

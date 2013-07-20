package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole;
import fi.foyt.fni.persistence.model.materials.UserMaterialRole_;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class UserMaterialRoleDAO extends GenericDAO<UserMaterialRole> {

	private static final long serialVersionUID = 1L;

	public UserMaterialRole create(Material material, User user, MaterialRole role) {
    EntityManager entityManager = getEntityManager();

    UserMaterialRole userMaterialRole = new UserMaterialRole();
    userMaterialRole.setMaterial(material);
    userMaterialRole.setUser(user);
    userMaterialRole.setRole(role);

    entityManager.persist(userMaterialRole);

    return userMaterialRole;
  }

	public UserMaterialRole findByMaterialAndUser(Material material, User user) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserMaterialRole> criteria = criteriaBuilder.createQuery(UserMaterialRole.class);
    Root<UserMaterialRole> root = criteria.from(UserMaterialRole.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(UserMaterialRole_.material), material),
          criteriaBuilder.equal(root.get(UserMaterialRole_.user), user)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

	public List<UserMaterialRole> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserMaterialRole> criteria = criteriaBuilder.createQuery(UserMaterialRole.class);
    Root<UserMaterialRole> root = criteria.from(UserMaterialRole.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(UserMaterialRole_.material), material)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<UserMaterialRole> listByMaterialAndRole(Material material, MaterialRole role) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserMaterialRole> criteria = criteriaBuilder.createQuery(UserMaterialRole.class);
    Root<UserMaterialRole> root = criteria.from(UserMaterialRole.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(UserMaterialRole_.material), material),
          criteriaBuilder.equal(root.get(UserMaterialRole_.role), role)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public UserMaterialRole updateRole(UserMaterialRole userMaterialRole, MaterialRole role) {
		userMaterialRole.setRole(role);
    getEntityManager().persist(userMaterialRole);
    return userMaterialRole;
  }

}

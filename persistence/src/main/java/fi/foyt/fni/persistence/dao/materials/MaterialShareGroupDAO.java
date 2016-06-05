package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup;
import fi.foyt.fni.persistence.model.materials.MaterialShareGroup_;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserGroup;
import fi.foyt.fni.persistence.model.users.UserGroupMember;
import fi.foyt.fni.persistence.model.users.UserGroupMember_;

public class MaterialShareGroupDAO extends GenericDAO<MaterialShareGroup> {

	private static final long serialVersionUID = 1L;

	public MaterialShareGroup create(Material material, UserGroup userGroup, MaterialRole role) {
    MaterialShareGroup MaterialShareUser = new MaterialShareGroup();
    MaterialShareUser.setMaterial(material);
    MaterialShareUser.setUserGroup(userGroup);
    MaterialShareUser.setRole(role);

    return persist(MaterialShareUser);
  }

	public MaterialShareGroup findByMaterialAndUserGroup(Material material, UserGroup userGroup) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareGroup> criteria = criteriaBuilder.createQuery(MaterialShareGroup.class);
    Root<MaterialShareGroup> root = criteria.from(MaterialShareGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(MaterialShareGroup_.material), material),
          criteriaBuilder.equal(root.get(MaterialShareGroup_.userGroup), userGroup)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public MaterialShareGroup findByMaterialAndUser(Material material, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareGroup> criteria = criteriaBuilder.createQuery(MaterialShareGroup.class);
    Root<MaterialShareGroup> shareGroupRoot = criteria.from(MaterialShareGroup.class);
    Root<UserGroupMember> groupMemberRoot = criteria.from(UserGroupMember.class);
    criteria.select(shareGroupRoot);
    
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(shareGroupRoot.get(MaterialShareGroup_.material), material),
        criteriaBuilder.equal(shareGroupRoot.get(MaterialShareGroup_.userGroup), groupMemberRoot.get(UserGroupMember_.group)),
        criteriaBuilder.equal(groupMemberRoot.get(UserGroupMember_.user), user)
      )
    );
    
    TypedQuery<MaterialShareGroup> query = entityManager.createQuery(criteria);
    
    return getSingleResult(query);
  }

	public List<MaterialShareGroup> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareGroup> criteria = criteriaBuilder.createQuery(MaterialShareGroup.class);
    Root<MaterialShareGroup> root = criteria.from(MaterialShareGroup.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialShareGroup_.material), material)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<MaterialShareGroup> listByMaterialAndRole(Material material, MaterialRole role) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareGroup> criteria = criteriaBuilder.createQuery(MaterialShareGroup.class);
    Root<MaterialShareGroup> root = criteria.from(MaterialShareGroup.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(MaterialShareGroup_.material), material),
          criteriaBuilder.equal(root.get(MaterialShareGroup_.role), role)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public MaterialShareGroup updateRole(MaterialShareGroup MaterialShareUser, MaterialRole role) {
		MaterialShareUser.setRole(role);
    getEntityManager().persist(MaterialShareUser);
    return MaterialShareUser;
  }

}

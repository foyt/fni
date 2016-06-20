package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialRole;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser;
import fi.foyt.fni.persistence.model.materials.MaterialShareUser_;
import fi.foyt.fni.persistence.model.users.User;

public class MaterialShareUserDAO extends GenericDAO<MaterialShareUser> {

	private static final long serialVersionUID = 1L;

	public MaterialShareUser create(Material material, User user, MaterialRole role) {
    EntityManager entityManager = getEntityManager();

    MaterialShareUser MaterialShareUser = new MaterialShareUser();
    MaterialShareUser.setMaterial(material);
    MaterialShareUser.setUser(user);
    MaterialShareUser.setRole(role);

    entityManager.persist(MaterialShareUser);

    return MaterialShareUser;
  }

	public MaterialShareUser findByMaterialAndUser(Material material, User user) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareUser> criteria = criteriaBuilder.createQuery(MaterialShareUser.class);
    Root<MaterialShareUser> root = criteria.from(MaterialShareUser.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(MaterialShareUser_.material), material),
          criteriaBuilder.equal(root.get(MaterialShareUser_.user), user)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

	public List<MaterialShareUser> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareUser> criteria = criteriaBuilder.createQuery(MaterialShareUser.class);
    Root<MaterialShareUser> root = criteria.from(MaterialShareUser.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialShareUser_.material), material)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public List<MaterialShareUser> listByMaterialAndRole(Material material, MaterialRole role) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialShareUser> criteria = criteriaBuilder.createQuery(MaterialShareUser.class);
    Root<MaterialShareUser> root = criteria.from(MaterialShareUser.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(MaterialShareUser_.material), material),
          criteriaBuilder.equal(root.get(MaterialShareUser_.role), role)
        )
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

	public MaterialShareUser updateRole(MaterialShareUser MaterialShareUser, MaterialRole role) {
		MaterialShareUser.setRole(role);
    getEntityManager().persist(MaterialShareUser);
    return MaterialShareUser;
  }

}

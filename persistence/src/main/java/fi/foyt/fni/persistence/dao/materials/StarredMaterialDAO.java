package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.StarredMaterial_;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.StarredMaterial;
import fi.foyt.fni.persistence.model.users.User;

public class StarredMaterialDAO extends GenericDAO<StarredMaterial> {

	private static final long serialVersionUID = 1L;

	public StarredMaterial create(Material material, User user, Date created) {
    EntityManager entityManager = getEntityManager();

    StarredMaterial starredMaterial = new StarredMaterial();
    starredMaterial.setMaterial(material);
    starredMaterial.setUser(user);
    starredMaterial.setCreated(created);
    
    entityManager.persist(starredMaterial);

    return starredMaterial;
  }

  public StarredMaterial findByMaterialAndUser(Material material, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StarredMaterial> criteria = criteriaBuilder.createQuery(StarredMaterial.class);
    Root<StarredMaterial> root = criteria.from(StarredMaterial.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(StarredMaterial_.material), material),
          criteriaBuilder.equal(root.get(StarredMaterial_.user), user)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<StarredMaterial> listByUserSortByCreated(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StarredMaterial> criteria = criteriaBuilder.createQuery(StarredMaterial.class);
    Root<StarredMaterial> root = criteria.from(StarredMaterial.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(StarredMaterial_.user), user)
    );
    
    criteria.orderBy(criteriaBuilder.desc(root.get(StarredMaterial_.created)));
    
    TypedQuery<StarredMaterial> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }

  public List<StarredMaterial> listByUserSortByCreated(User user, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StarredMaterial> criteria = criteriaBuilder.createQuery(StarredMaterial.class);
    Root<StarredMaterial> root = criteria.from(StarredMaterial.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(StarredMaterial_.user), user)
    );
    
    criteria.orderBy(criteriaBuilder.desc(root.get(StarredMaterial_.created)));
    
    TypedQuery<StarredMaterial> query = entityManager.createQuery(criteria);
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);

    return query.getResultList();
  }

	public List<StarredMaterial> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<StarredMaterial> criteria = criteriaBuilder.createQuery(StarredMaterial.class);
    Root<StarredMaterial> root = criteria.from(StarredMaterial.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(StarredMaterial_.material), material)
    );
    
    TypedQuery<StarredMaterial> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }

  public Long countByUser(User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<StarredMaterial> root = criteria.from(StarredMaterial.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(StarredMaterial_.user), user)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }

  public StarredMaterial updateCreated(StarredMaterial starredMaterial, Date created) {
    starredMaterial.setCreated(created);
    getEntityManager().persist(starredMaterial);
    return starredMaterial;
  }
}

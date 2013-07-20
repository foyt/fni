package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.MaterialView_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialView;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class MaterialViewDAO extends GenericDAO<MaterialView> {

	private static final long serialVersionUID = 1L;

	public MaterialView create(Material material, User user, Integer count, Date viewed) {
    EntityManager entityManager = getEntityManager();
    MaterialView materialView = new MaterialView();
    materialView.setMaterial(material);
    materialView.setUser(user);
    materialView.setCount(count);
    materialView.setViewed(viewed);
    
    entityManager.persist(materialView);

    return materialView;
  }

  public MaterialView findByMaterialAndUser(Material material, User user) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialView> criteria = criteriaBuilder.createQuery(MaterialView.class);
    Root<MaterialView> root = criteria.from(MaterialView.class);
    criteria.select(root);
    criteria.where(
        criteriaBuilder.and(
          criteriaBuilder.equal(root.get(MaterialView_.material), material),
          criteriaBuilder.equal(root.get(MaterialView_.user), user)
        )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<MaterialView> listByUserSortByViewed(User user, int firstResult, int maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialView> criteria = criteriaBuilder.createQuery(MaterialView.class);
    Root<MaterialView> root = criteria.from(MaterialView.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialView_.user), user)
    );
    criteria.orderBy(criteriaBuilder.desc(root.get(MaterialView_.viewed)));
    
    TypedQuery<MaterialView> query = entityManager.createQuery(criteria);
    
    query.setFirstResult(firstResult);
    query.setMaxResults(maxResults);
    
    return query.getResultList();
  }

	public List<MaterialView> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialView> criteria = criteriaBuilder.createQuery(MaterialView.class);
    Root<MaterialView> root = criteria.from(MaterialView.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialView_.material), material)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public MaterialView updateCount(MaterialView materialView, Integer count) {
    EntityManager entityManager = getEntityManager();

    materialView.setCount(count);
    materialView = entityManager.merge(materialView);
    return materialView;
  }

  public MaterialView updateViewed(MaterialView materialView, Date viewed) {
    EntityManager entityManager = getEntityManager();
    materialView.setViewed(viewed);
    materialView = entityManager.merge(materialView);
    return materialView;
  }
}

package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.PermaLink_;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.PermaLink;

public class PermaLinkDAO extends GenericDAO<PermaLink> {

	private static final long serialVersionUID = 1L;

	public PermaLink create(Material material, String path) {
    EntityManager entityManager = getEntityManager();

    PermaLink starredMaterial = new PermaLink();
    starredMaterial.setMaterial(material);
    starredMaterial.setPath(path);
    
    entityManager.persist(starredMaterial);

    return starredMaterial;
  }

  public PermaLink findByPath(String path) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PermaLink> criteria = criteriaBuilder.createQuery(PermaLink.class);
    Root<PermaLink> root = criteria.from(PermaLink.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PermaLink_.path), path)
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

	public List<PermaLink> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<PermaLink> criteria = criteriaBuilder.createQuery(PermaLink.class);
    Root<PermaLink> root = criteria.from(PermaLink.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(PermaLink_.material), material)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
  
}

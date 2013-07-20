package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.MaterialRevisionTag_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.materials.MaterialRevision;
import fi.foyt.fni.persistence.model.materials.MaterialRevisionTag;

@DAO
public class MaterialRevisionTagDAO extends GenericDAO<MaterialRevisionTag> {

	private static final long serialVersionUID = 1L;

	public MaterialRevisionTag create(MaterialRevision materialRevision, Tag tag, Boolean removed) {
    EntityManager entityManager = getEntityManager();

    MaterialRevisionTag materialRevisionTag = new MaterialRevisionTag();
    materialRevisionTag.setMaterialRevision(materialRevision);
    materialRevisionTag.setRemoved(removed);
    materialRevisionTag.setTag(tag);
    
    entityManager.persist(materialRevisionTag);

    return materialRevisionTag;
  }

  public List<MaterialRevisionTag> listByMaterialRevision(MaterialRevision materialRevision) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialRevisionTag> criteria = criteriaBuilder.createQuery(MaterialRevisionTag.class);
    Root<MaterialRevisionTag> root = criteria.from(MaterialRevisionTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialRevisionTag_.materialRevision), materialRevision)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

  public Long countByMaterialRevision(MaterialRevision materialRevision) {
  	EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<MaterialRevisionTag> root = criteria.from(MaterialRevisionTag.class);
    criteria.select(criteriaBuilder.count(root));
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialRevisionTag_.materialRevision), materialRevision)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}

package fi.foyt.fni.persistence.dao.materials;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.materials.MaterialThumbnail_;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.ImageSize;
import fi.foyt.fni.persistence.model.materials.Material;
import fi.foyt.fni.persistence.model.materials.MaterialThumbnail;

public class MaterialThumbnailDAO extends GenericDAO<MaterialThumbnail> {

	private static final long serialVersionUID = 1L;

	public MaterialThumbnail create(Material material, ImageSize size, byte[] content, String contentType) {
    EntityManager entityManager = getEntityManager();
    MaterialThumbnail materialThumbnail = new MaterialThumbnail();
    materialThumbnail.setContent(content);
    materialThumbnail.setContentType(contentType);
    materialThumbnail.setMaterial(material);
    materialThumbnail.setSize(size.toString());

    entityManager.persist(materialThumbnail);

    return materialThumbnail;
  }

  public MaterialThumbnail findByMaterialAndSize(Material material, ImageSize size) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialThumbnail> criteria = criteriaBuilder.createQuery(MaterialThumbnail.class);
    Root<MaterialThumbnail> root = criteria.from(MaterialThumbnail.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(MaterialThumbnail_.material), material),
        criteriaBuilder.equal(root.get(MaterialThumbnail_.size), size.toString())
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }

	public List<MaterialThumbnail> listByMaterial(Material material) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MaterialThumbnail> criteria = criteriaBuilder.createQuery(MaterialThumbnail.class);
    Root<MaterialThumbnail> root = criteria.from(MaterialThumbnail.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MaterialThumbnail_.material), material)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }

}

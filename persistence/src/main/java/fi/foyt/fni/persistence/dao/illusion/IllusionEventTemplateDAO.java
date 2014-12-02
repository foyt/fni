package fi.foyt.fni.persistence.dao.illusion;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventTemplate;
import fi.foyt.fni.persistence.model.illusion.IllusionEventTemplate_;

public class IllusionEventTemplateDAO extends GenericDAO<IllusionEventTemplate> {

	private static final long serialVersionUID = 1L;

	public IllusionEventTemplate create(IllusionEvent event, String name, String data, Date modified) {
	  IllusionEventTemplate illusionEventTemplate = new IllusionEventTemplate();

    illusionEventTemplate.setEvent(event);
    illusionEventTemplate.setName(name);
    illusionEventTemplate.setData(data);
    illusionEventTemplate.setModified(modified);
    
		return persist(illusionEventTemplate);
	}

  public IllusionEventTemplate findByEventAndName(IllusionEvent event, String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventTemplate> criteria = criteriaBuilder.createQuery(IllusionEventTemplate.class);
    Root<IllusionEventTemplate> root = criteria.from(IllusionEventTemplate.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(IllusionEventTemplate_.event), event),
        criteriaBuilder.equal(root.get(IllusionEventTemplate_.name), name)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public IllusionEventTemplate findByEventIsNullAndName(String name) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventTemplate> criteria = criteriaBuilder.createQuery(IllusionEventTemplate.class);
    Root<IllusionEventTemplate> root = criteria.from(IllusionEventTemplate.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.isNull(root.get(IllusionEventTemplate_.event)),
        criteriaBuilder.equal(root.get(IllusionEventTemplate_.name), name)
      )
    );

    return getSingleResult(entityManager.createQuery(criteria));
  }

  public List<IllusionEventTemplate> listByEvent(IllusionEvent event) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventTemplate> criteria = criteriaBuilder.createQuery(IllusionEventTemplate.class);
    Root<IllusionEventTemplate> root = criteria.from(IllusionEventTemplate.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventTemplate_.event), event)
    );

    return entityManager.createQuery(criteria).getResultList();
  }

	public IllusionEventTemplate updateName(IllusionEventTemplate template, String name) {
	  template.setName(name);
		return persist(template);
	}

  public IllusionEventTemplate updateData(IllusionEventTemplate template, String data) {
    template.setData(data);
    return persist(template);
  }

  public IllusionEventTemplate updateModified(IllusionEventTemplate template, Date modified) {
    template.setModified(modified);
    return persist(template);
  }
}

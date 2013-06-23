package fi.foyt.fni.persistence.dao.common;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.common.Tag_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Tag;

@RequestScoped
@DAO
public class TagDAO extends GenericDAO<Tag> {

	public Tag create(String text) {
    EntityManager entityManager = getEntityManager();

    Tag tag = new Tag();
    tag.setText(text);

    entityManager.persist(tag);

    return tag;
  }

  public Tag findByText(String text) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tag> criteria = criteriaBuilder.createQuery(Tag.class);
    Root<Tag> root = criteria.from(Tag.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Tag_.text), text));

    return getSingleResult(entityManager.createQuery(criteria));
  }

}

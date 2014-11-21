package fi.foyt.fni.persistence.dao.illusion;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.Genre;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGenre;
import fi.foyt.fni.persistence.model.illusion.IllusionEventGenre_;

public class IllusionEventGenreDAO extends GenericDAO<IllusionEventGenre> {

  private static final long serialVersionUID = 5537410044666830370L;

  public IllusionEventGenre create(IllusionEvent event, Genre genre) {
	  IllusionEventGenre illusionEventGenre = new IllusionEventGenre();
	  illusionEventGenre.setEvent(event);
	  illusionEventGenre.setGenre(genre);
	  return persist(illusionEventGenre);
	}
  
  public List<IllusionEventGenre> listByEvent(IllusionEvent event) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<IllusionEventGenre> criteria = criteriaBuilder.createQuery(IllusionEventGenre.class);
    Root<IllusionEventGenre> root = criteria.from(IllusionEventGenre.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(IllusionEventGenre_.event), event)
    );

    return entityManager.createQuery(criteria).getResultList();
  }
	
}

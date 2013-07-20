package fi.foyt.fni.persistence.dao.materials;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Image;
import fi.foyt.fni.persistence.model.users.User;

@DAO
public class BinaryDAO extends GenericDAO<Image> {

  private static final long serialVersionUID = 1L;

	public Long lengthDataByCreator(User creator) {
    // Criteria API does not support "length" operation for byte arrays
    // so we use JPQL queries
    EntityManager entityManager = getEntityManager();
    Query query = entityManager.createQuery("select coalesce(sum(length(data)), 0) from Binary where creator = :creator");
    query.setParameter("creator", creator);
    return (Long) query.getSingleResult();
  }

}

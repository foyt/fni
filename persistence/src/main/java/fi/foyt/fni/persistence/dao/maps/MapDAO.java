package fi.foyt.fni.persistence.dao.maps;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.illusion.IllusionSession;
import fi.foyt.fni.persistence.model.maps.Map;
import fi.foyt.fni.persistence.model.maps.Map_;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@DAO
public class MapDAO extends GenericDAO<Map> {
  
  public Map create(IllusionSession illusionSession, String name, User creator) {
    Date now = new Date();
    return create(illusionSession, name, creator, now, creator, now);
  }

	public Map create(IllusionSession illusionSession, String name, User creator, Date created, User modifier, Date modified) {
    EntityManager entityManager = getEntityManager();

    Map map = new Map();
    map.setCreated(created);
    map.setCreator(creator);
    map.setIllusionSession(illusionSession);
    map.setModified(modified);
    map.setModifier(modifier);
    map.setName(name);
    
    entityManager.persist(map);

    return map;
  }

  public List<Map> listByIllusionSession(IllusionSession illusionSession) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Map> criteria = criteriaBuilder.createQuery(Map.class);
    Root<Map> root = criteria.from(Map.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(Map_.illusionSession), illusionSession)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
}

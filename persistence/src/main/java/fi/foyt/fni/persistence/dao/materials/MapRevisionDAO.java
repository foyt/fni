package fi.foyt.fni.persistence.dao.materials;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.materials.Map;
import fi.foyt.fni.persistence.model.materials.MapRevision;
import fi.foyt.fni.persistence.model.materials.MapRevision_;

@DAO
public class MapRevisionDAO extends GenericDAO<MapRevision> {

	private static final long serialVersionUID = 1L;

	public MapRevision create(Map map, Long revision, Date created, Boolean compressed, Boolean completeRevision, byte[] data, String checksum, String sessionId) {
    MapRevision mapRevision = new MapRevision();
    mapRevision.setCreated(created);
    mapRevision.setCompleteRevision(completeRevision);
    mapRevision.setCompressed(compressed);
    mapRevision.setCreated(created);
    mapRevision.setData(data);
    mapRevision.setMap(map);
    mapRevision.setRevision(revision);
    mapRevision.setChecksum(checksum);
    mapRevision.setSessionId(sessionId);
    
    return persist(mapRevision);
  }
	
	public List<MapRevision> listByMap(Map map) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MapRevision> criteria = criteriaBuilder.createQuery(MapRevision.class);
    Root<MapRevision> root = criteria.from(MapRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MapRevision_.map), map)
    );
    
    return entityManager.createQuery(criteria).getResultList();
	}

	public  List<MapRevision> listByMapAndRevisionGreaterThan(Map map, Long revision) {
		EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MapRevision> criteria = criteriaBuilder.createQuery(MapRevision.class);
    Root<MapRevision> root = criteria.from(MapRevision.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
    		criteriaBuilder.equal(root.get(MapRevision_.map), map),
    		criteriaBuilder.greaterThan(root.get(MapRevision_.revision), revision)
    	)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public Long maxRevisionByMap(Map map) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> criteria = criteriaBuilder.createQuery(Long.class);
    Root<MapRevision> root = criteria.from(MapRevision.class);
    criteria.select(criteriaBuilder.max(root.get(MapRevision_.revision)));
    criteria.where(
      criteriaBuilder.equal(root.get(MapRevision_.map), map)
    );
    
    return entityManager.createQuery(criteria).getSingleResult();
  }
}

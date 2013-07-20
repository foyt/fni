package fi.foyt.fni.persistence.dao.maps;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.foyt.fni.persistence.model.maps.MapTileTag_;
import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.common.Tag;
import fi.foyt.fni.persistence.model.maps.MapTile;
import fi.foyt.fni.persistence.model.maps.MapTileTag;

@DAO
public class MapTileTagDAO extends GenericDAO<MapTileTag> {

	private static final long serialVersionUID = 1L;

	public MapTileTag create(MapTile mapTile, Tag tag) {
    EntityManager entityManager = getEntityManager();

    MapTileTag mapTileTag = new MapTileTag();
    mapTileTag.setMapTile(mapTile);
    mapTileTag.setTag(tag);
    
    entityManager.persist(mapTileTag);

    return mapTileTag;
  }

  public List<MapTileTag> listByMapTile(MapTile mapTile) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MapTileTag> criteria = criteriaBuilder.createQuery(MapTileTag.class);
    Root<MapTileTag> root = criteria.from(MapTileTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.equal(root.get(MapTileTag_.mapTile), mapTile)
    );
    
    return entityManager.createQuery(criteria).getResultList();
  }
  
  public MapTileTag findByMapTileAndTag(MapTile mapTile, Tag tag) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<MapTileTag> criteria = criteriaBuilder.createQuery(MapTileTag.class);
    Root<MapTileTag> root = criteria.from(MapTileTag.class);
    criteria.select(root);
    criteria.where(
      criteriaBuilder.and(
        criteriaBuilder.equal(root.get(MapTileTag_.mapTile), mapTile),
        criteriaBuilder.equal(root.get(MapTileTag_.tag), tag)
      )
    );
    
    return getSingleResult(entityManager.createQuery(criteria));
  }
  

}

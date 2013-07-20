package fi.foyt.fni.persistence.dao.maps;

import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.maps.MapTile;

@DAO
public class MapTileDAO extends GenericDAO<MapTile> {

	private static final long serialVersionUID = 1L;

	public MapTile create(String title, String fileName, String contentType) {
    EntityManager entityManager = getEntityManager();

    MapTile mapTile = new MapTile();
    mapTile.setTitle(title);
    mapTile.setFileName(fileName);
    mapTile.setContentType(contentType);
    
    entityManager.persist(mapTile);

    return mapTile;
  }
  
}

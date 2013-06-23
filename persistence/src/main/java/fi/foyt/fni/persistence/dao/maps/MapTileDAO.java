package fi.foyt.fni.persistence.dao.maps;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.maps.MapTile;

@RequestScoped
@DAO
public class MapTileDAO extends GenericDAO<MapTile> {

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

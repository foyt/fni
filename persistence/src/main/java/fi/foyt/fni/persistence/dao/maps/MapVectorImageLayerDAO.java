package fi.foyt.fni.persistence.dao.maps;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;

import fi.foyt.fni.persistence.dao.DAO;
import fi.foyt.fni.persistence.dao.GenericDAO;
import fi.foyt.fni.persistence.model.maps.Map;
import fi.foyt.fni.persistence.model.maps.MapVectorImageLayer;
import fi.foyt.fni.persistence.model.materials.VectorImage;

@RequestScoped
@DAO
public class MapVectorImageLayerDAO extends GenericDAO<MapVectorImageLayer> {
  
	public MapVectorImageLayer create(Map map, VectorImage vectorImage, String name) {
    EntityManager entityManager = getEntityManager();

    MapVectorImageLayer mapVectorImageLayer = new MapVectorImageLayer();
    mapVectorImageLayer.setVectorImage(vectorImage);
    mapVectorImageLayer.setName(name);
    
    entityManager.persist(mapVectorImageLayer);
    
    map.addLayer(mapVectorImageLayer);
    entityManager.persist(map);

    return mapVectorImageLayer;
  }
	
	@Override
	public void delete(MapVectorImageLayer mapVectorImageLayer) {
	  EntityManager entityManager = getEntityManager();
	  
	  if (mapVectorImageLayer.getMap() != null) {
	    Map map = mapVectorImageLayer.getMap();
	    map.removeLayer(mapVectorImageLayer);
	    entityManager.persist(map); 
	  }

	  super.delete(mapVectorImageLayer);
	}
  
}

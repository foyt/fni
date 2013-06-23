package fi.foyt.fni.persistence.model.maps;

import javax.persistence.Entity;

@Entity
public class MapTokenLayer extends MapLayer {
  
  public MapTokenLayer() {
    setType(MapLayerType.TOKEN);
  }

}

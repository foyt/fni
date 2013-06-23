package fi.foyt.fni.persistence.model.maps;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.materials.VectorImage;

@Entity
public class MapVectorImageLayer extends MapLayer {
  
  public MapVectorImageLayer() {
    setType(MapLayerType.VECTOR_IMAGE);
  }
  
  public VectorImage getVectorImage() {
    return vectorImage;
  }
  
  public void setVectorImage(VectorImage vectorImage) {
    this.vectorImage = vectorImage;
  }
  
  @ManyToOne
  private VectorImage vectorImage;
}

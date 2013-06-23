package fi.foyt.fni.persistence.model.maps;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.common.Tag;

@Entity
public class MapTileTag {

  public Long getId() {
    return id;
  }
  
  public MapTile getMapTile() {
    return mapTile;
  }
  
  public void setMapTile(MapTile mapTile) {
    this.mapTile = mapTile;
  }
  
  public Tag getTag() {
    return tag;
  }
  
  public void setTag(Tag tag) {
    this.tag = tag;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private MapTile mapTile;
  
  @ManyToOne
  private Tag tag;
}
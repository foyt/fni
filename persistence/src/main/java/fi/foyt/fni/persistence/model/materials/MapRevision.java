package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class MapRevision extends MaterialRevision {
  
  public Map getMap() {
    return map;
  }
  
  public void setMap(Map map) {
    this.map = map;
  }

  @ManyToOne
  private Map map;
}

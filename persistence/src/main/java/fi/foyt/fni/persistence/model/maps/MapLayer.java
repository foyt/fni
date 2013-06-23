package fi.foyt.fni.persistence.model.maps;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Inheritance (strategy = InheritanceType.JOINED)
public class MapLayer {
  
  public Long getId() {
    return id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public Map getMap() {
    return map;
  }
  
  public void setMap(Map map) {
    this.map = map;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public MapLayerType getType() {
    return type;
  }
  
  public void setType(MapLayerType type) {
    this.type = type;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  private String name;

  @ManyToOne
  private Map map;
  
  @Transient
  private MapLayerType type;
}

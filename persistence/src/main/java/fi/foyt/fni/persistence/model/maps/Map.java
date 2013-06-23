package fi.foyt.fni.persistence.model.maps;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceException;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.illusion.IllusionSession;
import fi.foyt.fni.persistence.model.users.User;

@Entity
public class Map {

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public IllusionSession getIllusionSession() {
    return illusionSession;
  }

  public void setIllusionSession(IllusionSession illusionSession) {
    this.illusionSession = illusionSession;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public Double getMillimetersPerPoint() {
    return millimetersPerPoint;
  }
  
  public void setMillimetersPerPoint(Double millimetersPerPoint) {
    this.millimetersPerPoint = millimetersPerPoint;
  }

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public User getModifier() {
    return modifier;
  }

  public void setModifier(User modifier) {
    this.modifier = modifier;
  }
  
  public List<MapLayer> getLayers() {
    return layers;
  }
  
  public void setLayers(List<MapLayer> layers) {
    this.layers = layers;
  }
  
  public void addLayer(MapLayer layer) {
    if (this.layers.contains(layer)) {
      throw new PersistenceException("Map already contains this layer");
    } else {
      if (layer.getMap() != null) {
        layer.getMap().removeLayer(layer);
      }
      
      layer.setMap(this);
    }
  }

  public void removeLayer(MapLayer layer) {
    if (!this.layers.contains(layer)) {
      throw new PersistenceException("Map does not contain this layer");
    } else {
      layer.setMap(null);
    }
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private IllusionSession illusionSession;

  @NotNull
  @NotEmpty
  @Column(nullable = false)
  private String name;
  
  @NotNull
  @Column(nullable = false)
  private Double millimetersPerPoint;

  @NotNull
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date modified;

  @NotNull
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne
  private User creator;

  @ManyToOne
  private User modifier;
  
  @OneToMany (mappedBy = "map")
  @IndexColumn (name = "indexColumn")
  private List<MapLayer> layers = new ArrayList<MapLayer>();
}

package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import fi.foyt.fni.persistence.model.common.Tag;

@Entity
public class MaterialRevisionTag {

  public Long getId() {
    return id;
  }
  
  public MaterialRevision getMaterialRevision() {
	  return materialRevision;
  }
  
  public void setMaterialRevision(MaterialRevision materialRevision) {
	  this.materialRevision = materialRevision;
  }

  public Tag getTag() {
    return tag;
  }
  
  public void setTag(Tag tag) {
    this.tag = tag;
  }
  
  /**
   * Indicates whether revision tag was removed or added    
   * @return whether revision tag was removed or added 
   */
  public Boolean getRemoved() {
	  return removed;
  }
  
  /**
   * Sets whether revision tag was removed or added
   * @param removed whether revision tag was removed or added
   */
  public void setRemoved(Boolean removed) {
	  this.removed = removed;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private MaterialRevision materialRevision;
  
  @ManyToOne
  private Tag tag;
  
  @Column (nullable = false, columnDefinition = "BIT")
  @NotNull
  private Boolean removed;
}
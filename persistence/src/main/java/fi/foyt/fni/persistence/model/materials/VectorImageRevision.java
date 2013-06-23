package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class VectorImageRevision extends MaterialRevision {
  
  public VectorImage getVectorImage() {
    return vectorImage;
  }
  
  public void setVectorImage(VectorImage vectorImage) {
    this.vectorImage = vectorImage;
  }

  @ManyToOne
  private VectorImage vectorImage;
}

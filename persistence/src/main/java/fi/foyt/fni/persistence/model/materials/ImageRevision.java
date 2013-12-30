package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class ImageRevision extends MaterialRevision {
  
  public Image getImage() {
    return image;
  }
  
  public void setImage(Image image) {
    this.image = image;
  }

  @ManyToOne
  private Image image;
}

package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class IllusionFolder extends Folder {
  
  public IllusionFolder() {
    setType(MaterialType.ILLUSION_FOLDER);
  }
  
}
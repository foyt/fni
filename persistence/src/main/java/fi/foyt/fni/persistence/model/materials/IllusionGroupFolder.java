package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn (name="id")
public class IllusionGroupFolder extends Folder {
  
  public IllusionGroupFolder() {
    setType(MaterialType.ILLUSION_GROUP_FOLDER);
  }
  
}
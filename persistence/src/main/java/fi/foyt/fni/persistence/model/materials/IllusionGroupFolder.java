package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class IllusionGroupFolder extends Folder {
  
  public IllusionGroupFolder() {
    setType(MaterialType.ILLUSION_GROUP_FOLDER);
  }
  
}
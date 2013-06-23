package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class UbuntuOneRootFolder extends Folder {
  
  public UbuntuOneRootFolder() {
    setType(MaterialType.UBUNTU_ONE_ROOT_FOLDER);
  }

  public Long getGeneration() {
    return generation;
  }
  
  public void setGeneration(Long generation) {
    this.generation = generation;
  }
  
  public Date getLastSynchronized() {
    return lastSynchronized;
  }
  
  public void setLastSynchronized(Date lastSynchronized) {
    this.lastSynchronized = lastSynchronized;
  }
  
  private Long generation;
  private Date lastSynchronized;
}
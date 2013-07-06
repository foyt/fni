package fi.foyt.fni.rest.entities.materials;

import java.util.Date;

public class UbuntuOneRootFolder extends Folder {
  
  public UbuntuOneRootFolder() {
    setType("UBUNTU_ONE_ROOT_FOLDER");
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
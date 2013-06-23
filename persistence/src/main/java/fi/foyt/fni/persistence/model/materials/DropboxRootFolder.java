package fi.foyt.fni.persistence.model.materials;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class DropboxRootFolder extends Folder {
  
  public DropboxRootFolder() {
    setType(MaterialType.DROPBOX_ROOT_FOLDER);
  }

  public String getDeltaCursor() {
    return deltaCursor;
  }
  
  public void setDeltaCursor(String deltaCursor) {
    this.deltaCursor = deltaCursor;
  }
  
  public Date getLastSynchronized() {
    return lastSynchronized;
  }
  
  public void setLastSynchronized(Date lastSynchronized) {
    this.lastSynchronized = lastSynchronized;
  }
  
  private String deltaCursor;
  private Date lastSynchronized;
}
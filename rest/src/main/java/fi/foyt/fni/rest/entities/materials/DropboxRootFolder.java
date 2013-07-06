package fi.foyt.fni.rest.entities.materials;

import java.util.Date;

public class DropboxRootFolder extends Folder {

	public DropboxRootFolder() {
		setType("DROPBOX_ROOT_FOLDER");	
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
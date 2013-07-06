package fi.foyt.fni.rest.entities.materials;

public class DropboxFolder extends Folder {
	
	public DropboxFolder() {
		setType("DROPBOX_FOLDER");	
	}
  
  public String getDropboxPath() {
    return dropboxPath;
  }
  
  public void setDropboxPath(String dropboxPath) {
    this.dropboxPath = dropboxPath;
  }
  
  private String dropboxPath;
}
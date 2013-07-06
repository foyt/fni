package fi.foyt.fni.rest.entities.materials;

public class DropboxFile extends Material {
  
	public DropboxFile() {
		setType("DROPBOX_FILE");	
	}
	
  public String getDropboxPath() {
    return dropboxPath;
  }
  
  public void setDropboxPath(String dropboxPath) {
    this.dropboxPath = dropboxPath;
  }
  
  public String getMimeType() {
    return mimeType;
  }
  
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
  
  private String dropboxPath;
  
  private String mimeType;
}
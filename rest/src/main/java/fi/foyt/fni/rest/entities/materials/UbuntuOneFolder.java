package fi.foyt.fni.rest.entities.materials;

public class UbuntuOneFolder extends Folder {
  
  public UbuntuOneFolder() {
    setType("DROPBOX_FOLDER");
  }
  
  public String getUbuntuOneKey() {
    return ubuntuOneKey;
  }
  
  public void setUbuntuOneKey(String ubuntuOneKey) {
    this.ubuntuOneKey = ubuntuOneKey;
  }
  
  public Long getGeneration() {
    return generation;
  }
  
  public void setGeneration(Long generation) {
    this.generation = generation;
  }
  
  public String getContentPath() {
    return contentPath;
  }
  
  public void setContentPath(String contentPath) {
    this.contentPath = contentPath;
  }
  
  private String ubuntuOneKey;
  
  private Long generation;

  private String contentPath;
}
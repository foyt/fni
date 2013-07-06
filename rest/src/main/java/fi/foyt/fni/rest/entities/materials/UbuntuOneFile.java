package fi.foyt.fni.rest.entities.materials;

public class UbuntuOneFile extends Material {
  
  public UbuntuOneFile() {
    setType("UBUNTU_ONE_FILE");
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
  
  public String getMimeType() {
    return mimeType;
  }
  
  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }
  
  private String ubuntuOneKey;
  
  private Long generation;

  private String contentPath;
  
  private String mimeType;
}
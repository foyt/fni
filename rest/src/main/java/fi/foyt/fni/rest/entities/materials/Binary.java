package fi.foyt.fni.rest.entities.materials;

public class Binary extends Material {
  
	public Binary() {
		setType("BINARY");
  }
	
  public String getContentType() {
    return contentType;
  }
 
  public void setContentType(String contentType) {
    this.contentType = contentType;
  } 
  
  private String contentType; 
}

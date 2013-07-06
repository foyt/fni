package fi.foyt.fni.rest.entities.materials;

public class GoogleDocument extends Material {
  
  public GoogleDocument() {
    setType("GOOGLE_DOCUMENT");
  }
  
  public String getDocumentId() {
    return documentId;
  }
  
  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }
  
  public String getDocumentType() {
		return documentType;
	}
  
  public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
  
  private String documentId;
  
  private String documentType;
}
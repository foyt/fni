package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity 
@Table (name = "Binary_")
@PrimaryKeyJoinColumn (name="id")
public class Binary extends Material {
  
  public Binary() {
    setType(MaterialType.BINARY);
  }
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public String getContentType() {
    return contentType;
  }
 
  public void setContentType(String contentType) {
    this.contentType = contentType;
  } 
  
  @Basic (fetch = FetchType.LAZY)
  @Column (length=1073741824)
  private byte[] data;
  
  private String contentType; 
}

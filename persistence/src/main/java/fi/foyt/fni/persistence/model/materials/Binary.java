package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity 
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
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
  
  @Basic
  @Lob
  private byte[] data;
  
  private String contentType; 
}

package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
@Entity
public class MaterialThumbnail {

  public Long getId() {
    return id;
  }
  
  public Material getMaterial() {
    return material;
  }
  
  public void setMaterial(Material material) {
    this.material = material;
  }
  
  public String getSize() {
    return size;
  }
  
  public void setSize(String size) {
    this.size = size;
  }
  
  public byte[] getContent() {
    return content;
  }
  
  public void setContent(byte[] content) {
    this.content = content;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private Material material;
  
  @Column(nullable = false)
  private String size;

  @Lob
  private byte[] content;

  private String contentType;
}

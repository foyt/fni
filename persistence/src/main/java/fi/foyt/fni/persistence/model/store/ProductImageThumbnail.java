package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
@Entity
public class ProductImageThumbnail {

  public Long getId() {
    return id;
  }
  
  public ProductImage getProductImage() {
		return productImage;
	}
  
  public void setProductImage(ProductImage productImage) {
		this.productImage = productImage;
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
  private ProductImage productImage;
  
  @Column(nullable = false)
  @NotNull
  @NotEmpty
  private String size;

  @Column(nullable = false)
  @Lob
  @NotNull
  private byte[] content;

  @Column(nullable = false)
  @NotNull
  @NotEmpty
  private String contentType;
}

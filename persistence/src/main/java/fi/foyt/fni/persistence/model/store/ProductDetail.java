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
public class ProductDetail {

  public Long getId() {
    return id;
  }
  
  public Product getProduct() {
		return product;
	}
  
  public void setProduct(Product product) {
		this.product = product;
	}
  
  public StoreDetail getDetail() {
		return detail;
	}
  
  public void setDetail(StoreDetail detail) {
		this.detail = detail;
	}
  
  public String getValue() {
		return value;
	}
  
  public void setValue(String value) {
		this.value = value;
	}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  @Lob
  private String value;

  @ManyToOne
  private Product product;
  
  @ManyToOne
  private StoreDetail detail;
}

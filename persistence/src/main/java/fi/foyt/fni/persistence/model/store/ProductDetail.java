package fi.foyt.fni.persistence.model.store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

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

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Product product;
  
  @ManyToOne
  private StoreDetail detail;
}

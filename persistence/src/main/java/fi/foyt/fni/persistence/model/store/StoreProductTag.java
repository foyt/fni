package fi.foyt.fni.persistence.model.store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table (
  uniqueConstraints = {
    @UniqueConstraint (columnNames = {"product_id", "tag_id"})
  }    
)
public class StoreProductTag {

  public Long getId() {
    return id;
  }

  public StoreProduct getProduct() {
    return product;
  }
  
  public void setProduct(StoreProduct product) {
    this.product = product;
  }
  
  public StoreTag getTag() {
    return tag;
  }
  
  public void setTag(StoreTag tag) {
    this.tag = tag;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private StoreProduct product;
  
  @ManyToOne
  private StoreTag tag;
}

package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DeliveryPrice {

  public Long getId() {
    return id;
  }
  
  public Double getFromWeight() {
    return fromWeight;
  }
  
  public void setFromWeight(Double fromWeight) {
    this.fromWeight = fromWeight;
  }
  
  public Double getToWeight() {
    return toWeight;
  }
  
  public void setToWeight(Double toWeight) {
    this.toWeight = toWeight;
  }
  
  public Double getPrice() {
    return price;
  }
  
  public void setPrice(Double price) {
    this.price = price;
  }
  
  public DeliveryMethod getDeliveryMethod() {
		return deliveryMethod;
	}
  
  public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  private Double fromWeight;
  
  private Double toWeight;
  
  @ManyToOne
  private DeliveryMethod deliveryMethod;
  
  @Column (nullable = false)
  private Double price;
}

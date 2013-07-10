package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class DeliveryPrice {

  public Long getId() {
    return id;
  }
  
  public Integer getFromWeight() {
		return fromWeight;
	}
  
  public void setFromWeight(Integer fromWeight) {
		this.fromWeight = fromWeight;
	}
  
  public Integer getToWeight() {
		return toWeight;
	}
  
  public void setToWeight(Integer toWeight) {
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
  
  @NotNull
  @Column (nullable = false)
  private Integer fromWeight;
  
  @NotNull
  @Column (nullable = false)
  private Integer toWeight;
  
  @ManyToOne
  private DeliveryMethod deliveryMethod;
  
  @NotNull
  @Column (nullable = false)
  private Double price;
}

package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class OrderItem {
  
  public Long getId() {
    return id;
  }
  
  public Order getOrder() {
    return order;
  }
  
  public void setOrder(Order order) {
    this.order = order;
  }
  
  public Integer getCount() {
    return count;
  }
  
  public void setCount(Integer count) {
    this.count = count;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public Publication getProduct() {
    return publication;
  }
  
  public void setProduct(Publication publication) {
    this.publication = publication;
  }
  
  public Double getUnitPrice() {
    return unitPrice;
  }
  
  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne 
  private Order order;
  
  @NotEmpty
  @NotNull
  @Column (nullable = false)
  private String name;
  
  @ManyToOne 
  private Publication publication;
    
  private Double unitPrice;
  
  @NotNull
  @Column (nullable = false)
  private Integer count;
}

package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ShoppingCartItem {

  public Long getId() {
    return id;
  }
  
  public ShoppingCart getCart() {
    return cart;
  }
  
  public void setCart(ShoppingCart cart) {
    this.cart = cart;
  }
  
  public Integer getCount() {
    return count;
  }
  
  public void setCount(Integer count) {
    this.count = count;
  }
  
  public Publication getProduct() {
		return publication;
	}
  
  public void setProduct(Publication publication) {
		this.publication = publication;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne 
  private ShoppingCart cart;
  
  @Column (nullable = false)
  private Integer count;
  
  @ManyToOne 
  private Publication publication;
}

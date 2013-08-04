package fi.foyt.fni.persistence.model.gamelibrary;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductTag {

  public Long getId() {
    return id;
  }
  
  public Product getProduct() {
		return product;
	}
  
  public void setProduct(Product product) {
		this.product = product;
	}
  
  public GameLibraryTag getTag() {
		return tag;
	}
  
  public void setTag(GameLibraryTag tag) {
		this.tag = tag;
	}

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  private Product product;
  
  @ManyToOne
  private GameLibraryTag tag;
}

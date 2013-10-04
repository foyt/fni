package fi.foyt.fni.persistence.model.gamelibrary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.User;

@Entity
public class ShoppingCart {

  public Long getId() {
    return id;
  }
  
  public User getCustomer() {
    return customer;
  }
  
  public void setCustomer(User customer) {
    this.customer = customer;
  }
  
  public String getSessionId() {
		return sessionId;
	}
  
  public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
  
  public Date getCreated() {
    return created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public Date getModified() {
    return modified;
  }
  
  public void setModified(Date modified) {
    this.modified = modified;
  }
  
  public String getDeliveryMethodId() {
		return deliveryMethodId;
	}
  
  public void setDeliveryMethodId(String deliveryMethodId) {
		this.deliveryMethodId = deliveryMethodId;
	}
  
  public Address getDeliveryAddress() {
    return deliveryAddress;
  }
  
  public void setDeliveryAddress(Address deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;
  
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date modified;  
  
  @ManyToOne 
  private User customer;
  
  private String sessionId;
  
  private String deliveryMethodId;
  
  @ManyToOne 
  private Address deliveryAddress;
}

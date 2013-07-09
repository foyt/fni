package fi.foyt.fni.persistence.model.store;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
  
  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }
  
  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
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
  
  public DeliveryMethod getDeliveryMethod() {
		return deliveryMethod;
	}
  
  public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
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
  
  @ManyToOne 
  private PaymentMethod paymentMethod;
  
  @ManyToOne 
  private DeliveryMethod deliveryMethod;
  
  @ManyToOne 
  private Address deliveryAddress;
}

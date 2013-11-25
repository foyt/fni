package fi.foyt.fni.persistence.model.gamelibrary;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import fi.foyt.fni.persistence.model.users.Address;
import fi.foyt.fni.persistence.model.users.User;

@Entity (name = "Order_")
public class Order {
  
  public Long getId() {
    return id;
  }
  
  public User getCustomer() {
    return customer;
  }
  
  public void setCustomer(User customer) {
    this.customer = customer;
  }
  
  public String getCustomerCompany() {
		return customerCompany;
	}

	public void setCustomerCompany(String customerCompany) {
		this.customerCompany = customerCompany;
	}

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}
  
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }
  
  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }
  
  public Double getShippingCosts() {
    return shippingCosts;
  }
  
  public void setShippingCosts(Double shippingCosts) {
    this.shippingCosts = shippingCosts;
  }
  
  public Date getCreated() {
    return created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public Date getPaid() {
    return paid;
  }
  
  public void setPaid(Date paid) {
    this.paid = paid;
  }
  
  public Date getShipped() {
		return shipped;
	}
  
  public void setShipped(Date shipped) {
		this.shipped = shipped;
	}
  
  public Date getDelivered() {
    return delivered;
  }
  
  public void setDelivered(Date delivered) {
    this.delivered = delivered;
  }
  
  public Date getCanceled() {
    return canceled;
  }
  
  public void setCanceled(Date canceled) {
    this.canceled = canceled;
  }
  
  public Address getDeliveryAddress() {
    return deliveryAddress;
  }
  
  public void setDeliveryAddress(Address deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }
  
  public String getNotes() {
    return notes;
  }
  
  public void setNotes(String notes) {
    this.notes = notes;
  }
  
  public String getAccessKey() {
    return accessKey;
  }
  
  public void setAccessKey(String accessKey) {
    this.accessKey = accessKey;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne 
  private User customer;
  
  private String customerCompany;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String customerFirstName;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String customerLastName;
  
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String customerEmail;
  
  private String customerPhone;
  
  private String customerMobile;
  
  @Enumerated (EnumType.STRING)
  private OrderStatus orderStatus;
  
  @ManyToOne
  private Address deliveryAddress; 

  private Double shippingCosts;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date paid;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date shipped;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date delivered;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date canceled;  
  
  @Basic
  @Lob
  private String notes;
  
  private String accessKey;
}

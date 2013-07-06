package fi.foyt.fni.persistence.model.store;

import java.util.Currency;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import fi.foyt.fni.persistence.model.users.User;

@Entity (name = "StoreOrder")
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
  
  public PaymentMethod getPaymentMethod() {
    return paymentMethod;
  }
  
  public void setPaymentMethod(PaymentMethod paymentMethod) {
    this.paymentMethod = paymentMethod;
  }
  
  public OrderStatus getOrderStatus() {
    return orderStatus;
  }
  
  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }
  
  public Currency getCurrency() {
    return currency;
  }
  
  public void setCurrency(Currency currency) {
    this.currency = currency;
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
  
  public Date getCheckedOut() {
    return checkedOut;
  }
  
  public void setCheckedOut(Date checkedOut) {
    this.checkedOut = checkedOut;
  }
  
  public Date getPaid() {
    return paid;
  }
  
  public void setPaid(Date paid) {
    this.paid = paid;
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
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne 
  private User customer;
  
  @ManyToOne 
  private PaymentMethod paymentMethod;
  
  @Enumerated (EnumType.STRING)
  private OrderStatus orderStatus;
  
  @ManyToOne
  private Address deliveryAddress;
  
  private Currency currency;  

  private Double shippingCosts;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date checkedOut;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date paid;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date delivered;
  
  @Temporal (TemporalType.TIMESTAMP)
  private Date canceled;  
  
  @Basic (fetch=FetchType.LAZY)
  @Lob
  private String notes;
}

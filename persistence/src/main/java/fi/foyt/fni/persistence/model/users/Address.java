package fi.foyt.fni.persistence.model.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.common.Country;

@Entity
public class Address {

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public AddressType getAddressType() {
    return addressType;
  }
  
  public void setAddressType(AddressType addressType) {
    this.addressType = addressType;
  }
  
  public String getStreet1() {
    return street1;
  }
  
  public void setStreet1(String street1) {
    this.street1 = street1;
  }
  
  public String getStreet2() {
    return street2;
  }
  
  public void setStreet2(String street2) {
    this.street2 = street2;
  }
  
  public String getCity() {
    return city;
  }
  
  public void setCity(String city) {
    this.city = city;
  }

  public Country getCountry() {
    return country;
  }
  
  public void setCountry(Country country) {
    this.country = country;
  }
  
  public String getPostalCode() {
    return postalCode;
  }
  
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne 
  private User user;
  
  @Column (nullable = false)
  @Enumerated
  private AddressType addressType;
  
  @Column (nullable = false)
  private String street1;
  
  private String street2;
  
  @Column (nullable = false)
  private String postalCode;
  
  @Column (nullable = false)
  private String city;
  
  @ManyToOne 
  private Country country;
}

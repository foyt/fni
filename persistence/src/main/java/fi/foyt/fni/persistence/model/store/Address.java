package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.model.common.Country;
import fi.foyt.fni.persistence.model.users.User;

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
  
  public String getPersonName() {
    return personName;
  }
  
  public void setPersonName(String personName) {
    this.personName = personName;
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
  
  public String getCompanyName() {
    return companyName;
  }
  
  public void setCompanyName(String companyName) {
    this.companyName = companyName;
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
  
  @Transient
  public boolean match(Object obj) {
    if (obj instanceof Address) {
      Address other = (Address) obj;
      
      Long countryId = this.getCountry() != null ? this.getCountry().getId() : null;
      Long otherCountryId = other.getCountry() != null ? other.getCountry().getId() : null;
      
      return getAddressType().equals(other.getAddressType()) &&
        StringUtils.equals(getPersonName(), other.getPersonName()) && 
        StringUtils.equals(getCompanyName(), other.getCompanyName()) &&
        StringUtils.equals(getStreet1(), other.getStreet1()) &&
        StringUtils.equals(getStreet2(), other.getStreet2()) && 
        StringUtils.equals(getPostalCode(), other.getPostalCode()) &&
        StringUtils.equals(getCity(), other.getCity()) &&
        countryId == otherCountryId;      
    } else {
      return false;
    }    
  }

  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne 
  private User user;
  
  @Column (nullable = false)
  @Enumerated
  private AddressType addressType;
  
  private String companyName;
  
  private String personName;
  
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

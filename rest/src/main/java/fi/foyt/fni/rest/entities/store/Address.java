package fi.foyt.fni.rest.entities.store;

public class Address {

  public Long getId() {
    return id;
  }
  
  public String getAddressType() {
		return addressType;
	}
  
  public void setAddressType(String addressType) {
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

  public String getCountry() {
    return country;
  }
  
  public void setCountry(String country) {
    this.country = country;
  }
  
  public String getPostalCode() {
    return postalCode;
  }
  
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }
  
  public String getRegion() {
    return region;
  }
  
  public void setRegion(String region) {
    this.region = region;
  }
  
  private Long id;
  
  private String addressType;
  
  private String companyName;
  
  private String personName;
  
  private String street1;
  
  private String street2;
  
  private String postalCode;
  
  private String city;
  
  private String region;
  
  private String country;
}

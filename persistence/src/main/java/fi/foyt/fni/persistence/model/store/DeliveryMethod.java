package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import fi.foyt.fni.persistence.model.common.MultilingualString;

@Entity
public class DeliveryMethod {

  public Long getId() {
    return id;
  }
  
  public Boolean getRequiresAddress() {
    return requiresAddress;
  }
  
  public void setRequiresAddress(Boolean requiresAddress) {
    this.requiresAddress = requiresAddress;
  }
  
  public Double getUnitPrice() {
    return unitPrice;
  }
  
  public void setUnitPrice(Double unitPrice) {
    this.unitPrice = unitPrice;
  }
  
  public MultilingualString getName() {
		return name;
	}
  
  public void setName(MultilingualString name) {
		this.name = name;
	}
  
  public MultilingualString getInfo() {
		return info;
	}
  
  public void setInfo(MultilingualString info) {
		this.info = info;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @OneToOne
  private MultilingualString name;
  
  @OneToOne
  private MultilingualString info;
  
  private Double unitPrice;
  
  @NotNull
  @Column (nullable = false, columnDefinition = "BIT")
  private Boolean requiresAddress;
}

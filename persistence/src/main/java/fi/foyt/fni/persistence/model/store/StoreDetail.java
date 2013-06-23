package fi.foyt.fni.persistence.model.store;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import fi.foyt.fni.persistence.model.common.MultilingualString;

@Entity
public class StoreDetail {

  public Long getId() {
    return id;
  }
  
  public MultilingualString getName() {
		return name;
	}
  
  public void setName(MultilingualString name) {
		this.name = name;
	}
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @OneToOne
  private MultilingualString name;
}

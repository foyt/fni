package fi.foyt.fni.persistence.model.store;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class StoreDetail {

  public Long getId() {
    return id;
  }
  
  public String getName() {
		return name;
	}
  
  public void setName(String name) {
		this.name = name;
	}
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable = false, unique = true)
  @NotEmpty
  @NotNull
  private String name;
}

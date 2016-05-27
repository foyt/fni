package fi.foyt.fni.persistence.model.users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.InheritanceType;
import javax.persistence.Inheritance;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Inheritance (strategy = InheritanceType.JOINED)
public class UserGroup {
	
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
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @NotNull
  @NotEmpty
  private String name;
}

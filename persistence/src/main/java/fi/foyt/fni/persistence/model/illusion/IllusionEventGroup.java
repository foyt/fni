package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class IllusionEventGroup {
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
    return name;
  }
	
	public void setName(String name) {
    this.name = name;
  }
	
	public IllusionEvent getEvent() {
    return event;
  }
	
	public void setEvent(IllusionEvent event) {
    this.event = event;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @NotNull
  @NotEmpty
  private String name;
 
  @ManyToOne
  private IllusionEvent event;
}

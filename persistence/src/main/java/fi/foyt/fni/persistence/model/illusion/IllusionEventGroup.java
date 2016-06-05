package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.UserGroup;

@Entity
public class IllusionEventGroup extends UserGroup {
	
	public IllusionEvent getEvent() {
    return event;
  }
	
	public void setEvent(IllusionEvent event) {
    this.event = event;
  }
 
  @ManyToOne
  private IllusionEvent event;
}

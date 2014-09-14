package fi.foyt.fni.persistence.model.illusion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class IllusionEventGroupMember {
	
	public Long getId() {
		return id;
	}
	
	public IllusionEventGroup getGroup() {
    return group;
  }
	
	public void setGroup(IllusionEventGroup group) {
    this.group = group;
  }
	
	public IllusionEventParticipant getParticipant() {
    return participant;
  }
	
	public void setParticipant(IllusionEventParticipant participant) {
    this.participant = participant;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private IllusionEventGroup group;

  @ManyToOne
  private IllusionEventParticipant participant;
}

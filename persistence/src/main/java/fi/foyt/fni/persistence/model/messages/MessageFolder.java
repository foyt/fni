package fi.foyt.fni.persistence.model.messages;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class MessageFolder {

	public Long getId() {
	  return id;
  }
	
	public String getName() {
	  return name;
  }
	
	public void setName(String name) {
	  this.name = name;
  }
	
	public User getOwner() {
	  return owner;
  }
	
	public void setOwner(User owner) {
	  this.owner = owner;
  }
	
	@Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable=false)
  private String name;
    
  @ManyToOne
  private User owner;
}

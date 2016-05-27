package fi.foyt.fni.persistence.model.users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class UserGroupMember {
	
	public Long getId() {
		return id;
	}
	
	public UserGroup getGroup() {
    return group;
  }
	
	public void setGroup(UserGroup group) {
    this.group = group;
  }
	
	public User getUser() {
    return user;
  }
	
	public void setUser(User user) {
    this.user = user;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private UserGroup group;

  @ManyToOne
  private User user;
}

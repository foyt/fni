package fi.foyt.fni.persistence.model.forum;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
public class ForumFollower {

  public Long getId() {
    return id;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Forum getForum() {
    return forum;
  }
  
  public void setForum(Forum forum) {
    this.forum = forum;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private User user;
  
  @ManyToOne
  private Forum forum;
}

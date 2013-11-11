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
public class ForumTopicFollower {

  public Long getId() {
    return id;
  }
  
  public ForumTopic getTopic() {
    return topic;
  }
  
  public void setTopic(ForumTopic topic) {
    this.topic = topic;
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
  private User user;
  
  @ManyToOne
  private ForumTopic topic;
}

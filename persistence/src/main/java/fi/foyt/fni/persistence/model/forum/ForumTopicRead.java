package fi.foyt.fni.persistence.model.forum;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Cacheable (true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class ForumTopicRead {

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
  
  public Date getTime() {
    return time;
  }
  
  public void setTime(Date time) {
    this.time = time;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  private User user;
  
  @ManyToOne
  private ForumTopic topic;
  
  @Column
  @Temporal (TemporalType.TIMESTAMP)
  private Date time;
}

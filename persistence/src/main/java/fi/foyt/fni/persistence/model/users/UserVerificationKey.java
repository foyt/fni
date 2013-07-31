package fi.foyt.fni.persistence.model.users;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Immutable;

@Entity
@Immutable
public class UserVerificationKey {

  public Long getId() {
    return id;
  }
  
  public String getValue() {
	  return value;
  }
  
  public void setValue(String value) {
	  this.value = value;
  }
  
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public Date getCreated() {
	  return created;
  }
  
  public void setCreated(Date created) {
	  this.created = created;
  }
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private User user;
 
  @Column (nullable = false)
  private String value;
  
  @Column (nullable = false)
  private Date created; 
}
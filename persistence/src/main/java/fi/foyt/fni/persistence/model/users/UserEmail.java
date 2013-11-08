package fi.foyt.fni.persistence.model.users;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Cacheable (true)
@Cache (usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Indexed
public class UserEmail {

  public Long getId() {
    return id;
  }
 
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public Boolean getPrimary() {
	  return primary;
  }
  
  public void setPrimary(Boolean primary) {
	  this.primary = primary;
  }
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private User user;
 
  @Column (nullable = false, unique = true)
  @NotNull
  @NotEmpty
  @Email
  @Field
  private String email;
  
  @Column (nullable = false, name = "primaryEmail", columnDefinition = "BIT")
  private Boolean primary;
}
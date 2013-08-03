package fi.foyt.fni.persistence.model.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class UserContactField {

  public Long getId() {
    return id;
  }
 
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public UserContactFieldType getType() {
		return type;
	}
  
  public void setType(UserContactFieldType type) {
		this.type = type;
	}
  
  public String getValue() {
		return value;
	}
  
  public void setValue(String value) {
		this.value = value;
	}
  
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private User user;
 
  @Column (nullable = false)
  @NotNull
  @NotEmpty
  private String value;
  
  @Enumerated (EnumType.STRING)
  @Column (nullable = false)
  @NotNull
  private UserContactFieldType type;
}
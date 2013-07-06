package fi.foyt.fni.rest.entities.users;

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
 
  private Long id;
 
  private User user;
 
  private String email;
  
  private Boolean primary;
}
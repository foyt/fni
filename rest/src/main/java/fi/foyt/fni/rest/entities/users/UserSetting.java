package fi.foyt.fni.rest.entities.users;

public class UserSetting {

  public Long getId() {
    return id;
  }
 
  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public String getKey() {
		return key;
	}
  
  public void setKey(String key) {
		this.key = key;
	}
  
  public String getValue() {
    return value;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
 
  private Long id;
 
  private User user;
 
  private String value;

  private String key;
}
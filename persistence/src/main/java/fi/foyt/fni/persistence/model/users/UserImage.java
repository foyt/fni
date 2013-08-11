package fi.foyt.fni.persistence.model.users;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class UserImage {
	
	public Long getId() {
		return id;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
  
  public byte[] getData() {
    return data;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public String getContentType() {
    return contentType;
  }
 
  public void setContentType(String contentType) {
    this.contentType = contentType;
  } 
 
  @Id
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
 
  @ManyToOne
  private User user;
  
  @Lob
  private byte[] data;
  
  private String contentType; 
}

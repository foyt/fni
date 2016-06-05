package fi.foyt.fni.persistence.model.materials;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import fi.foyt.fni.persistence.model.users.User;

@Entity
public class MaterialShareUser extends MaterialShare {

  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  @ManyToOne (optional = false)
  private User user;
}
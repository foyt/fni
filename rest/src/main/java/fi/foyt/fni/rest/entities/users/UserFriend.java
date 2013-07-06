package fi.foyt.fni.rest.entities.users;

// TODO: NOOOOOO...
public class UserFriend {

  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
  
  public User getFriend() {
    return friend;
  }
  
  public void setFriend(User friend) {
    this.friend = friend;
  }

  public Boolean getConfirmed() {
    return confirmed;
  }

  public void setConfirmed(Boolean confirmed) {
    this.confirmed = confirmed;
  }

  private Long id;

  private User user;

  private User friend;

  private Boolean confirmed;
}

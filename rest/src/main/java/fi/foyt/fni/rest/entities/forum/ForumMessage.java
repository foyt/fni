package fi.foyt.fni.rest.entities.forum;

import java.util.Date;

import fi.foyt.fni.rest.entities.users.User;

public class ForumMessage {

  public Long getId() {
    return id;
  }
  
  public Date getModified() {
    return modified;
  }
  
  public void setModified(Date modified) {
    this.modified = modified;
  }
  
  public Date getCreated() {
    return created;
  }
  
  public void setCreated(Date created) {
    this.created = created;
  }
  
  public User getAuthor() {
    return author;
  }
  
  public void setAuthor(User author) {
    this.author = author;
  }
  
  public Long getViews() {
    return views;
  }
  
  public void setViews(Long views) {
    this.views = views;
  }
  
  private Long id;
  
  private Date modified;
  
  private Date created;

  private User author;
  
  private Long views;
}

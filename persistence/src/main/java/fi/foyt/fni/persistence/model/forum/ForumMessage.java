package fi.foyt.fni.persistence.model.forum;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.search.annotations.DocumentId;

import fi.foyt.fni.persistence.model.users.User;

@Entity
@Inheritance (strategy=InheritanceType.JOINED)
@Cacheable (true)
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
  
  @Id
  @DocumentId
  @GeneratedValue (strategy=GenerationType.IDENTITY)
  private Long id;
  
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date modified;
  
  @Column (nullable=false)
  @Temporal (TemporalType.TIMESTAMP)
  private Date created;

  @ManyToOne
  private User author;
  
  @Column (nullable=false)
  private Long views = new Long(0);
}

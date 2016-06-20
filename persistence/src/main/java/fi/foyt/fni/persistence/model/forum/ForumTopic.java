package fi.foyt.fni.persistence.model.forum;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
@Cacheable (true)
@Table (
  uniqueConstraints = {
    @UniqueConstraint (columnNames = {"forum_id", "urlName"})
  }    
)
public class ForumTopic extends ForumMessage {

  public String getSubject() {
    return subject;
  }
  
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  public String getUrlName() {
    return urlName;
  }
  
  public void setUrlName(String urlName) {
    this.urlName = urlName;
  }
  
  public Forum getForum() {
    return forum;
  }
  
  public void setForum(Forum forum) {
    this.forum = forum;
  }
  
  @Transient
  public String getFullPath() {
  	return getForum().getUrlName() + "/" + getUrlName();
  }
  
  @ManyToOne
  private Forum forum;
  
  @Column (nullable=false)
  @Field
  private String subject;
  
  @Column (nullable=false)
  private String urlName;
}

package fi.foyt.fni.persistence.model.forum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Entity
@PrimaryKeyJoinColumn (name="id")
@Indexed
public class ForumPost extends ForumMessage {

  public ForumTopic getTopic() {
    return topic;
  }
  
  public void setTopic(ForumTopic topic) {
    this.topic = topic;
  }
  
  public String getContent() {
    return content;
  }
  
  public void setContent(String content) {
    this.content = content;
  }
  
  @Transient
  @Field
  public String getTopicSubject() {
  	return getTopic().getSubject();
  }
  
  @Transient 
  @Field
  public String getContentPlain() {
  	return StringEscapeUtils.unescapeHtml4(getContent().replaceAll("\\<.*?>",""));
  }
  
  @ManyToOne
  private ForumTopic topic;
  
  @Column (nullable=false)
  @Lob
  private String content;  
}

package fi.foyt.fni.rest.entities.forum;

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
  
  private ForumTopic topic;
  
  private String content;  
}

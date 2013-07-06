package fi.foyt.fni.rest.entities.forum;

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

  private Forum forum;
  
  private String subject;
  
  private String urlName;
}

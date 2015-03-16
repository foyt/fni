package fi.foyt.fni.rest.forum.model;

public class ForumTopicWatcher {

  public ForumTopicWatcher() {
    super();
  }
  
  public ForumTopicWatcher(Long id, Long userId) {
    this();
    this.id = id;
    this.userId = userId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  private Long id;
  private Long userId;
}

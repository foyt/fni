package fi.foyt.fni.rest.forum.model;

import java.util.Date;

public class ForumPost {

  public ForumPost() {
  }

  public ForumPost(Long id, Long topicId, String content, Date modified, Date created, Long authorId, Long views) {
    super();
    this.id = id;
    this.topicId = topicId;
    this.content = content;
    this.modified = modified;
    this.created = created;
    this.authorId = authorId;
    this.views = views;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTopicId() {
    return topicId;
  }

  public void setTopicId(Long topicId) {
    this.topicId = topicId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  public Long getViews() {
    return views;
  }

  public void setViews(Long views) {
    this.views = views;
  }

  private Long id;
  private Long topicId;
  private String content;
  private Date modified;
  private Date created;
  private Long authorId;
  private Long views;
}

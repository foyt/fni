package fi.foyt.fni.rest.forum.model;

import java.time.OffsetDateTime;

public class ForumPost {

  public ForumPost() {
  }

  public ForumPost(Long id, Long topicId, String content, OffsetDateTime modified, OffsetDateTime created, Long authorId, Long views) {
    super();
    this.id = id;
    this.topicId = topicId;
    this.content = content;
    this.modified = modified;
    this.created = created;
    this.authorId = authorId;
    this.views = views;
  }

  /**
   * Returns forum post id
   * 
   * @return forum post id
   */
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Returns forum topic id
   * 
   * @return forum topic id
   */
  public Long getTopicId() {
    return topicId;
  }

  public void setTopicId(Long topicId) {
    this.topicId = topicId;
  }

  /**
   * Returns forum post content
   * 
   * @return forum post content
   */
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Returns last time when the post was removed
   * 
   * @return last time when the post was removed
   */
  public OffsetDateTime getModified() {
    return modified;
  }

  public void setModified(OffsetDateTime modified) {
    this.modified = modified;
  }

  /**
   * Returns when the post was created
   * 
   * @return when the post was created
   */
  public OffsetDateTime getCreated() {
    return created;
  }

  public void setCreated(OffsetDateTime created) {
    this.created = created;
  }

  /**
   * Returns post author's id
   * 
   * @return post author's id
   */
  public Long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(Long authorId) {
    this.authorId = authorId;
  }

  /**
   * Returns post view count
   * 
   * @return post view count
   */
  public Long getViews() {
    return views;
  }

  public void setViews(Long views) {
    this.views = views;
  }

  private Long id;
  private Long topicId;
  private String content;
  private OffsetDateTime modified;
  private OffsetDateTime created;
  private Long authorId;
  private Long views;
}

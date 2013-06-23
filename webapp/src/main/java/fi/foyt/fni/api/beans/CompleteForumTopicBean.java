package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.forum.ForumTopic;

public class CompleteForumTopicBean {
  public CompleteForumTopicBean(Long id, Date modified, Date created, CompactUserBean author, Long views, CompactForumBean forum, String subject,
      String urlName, String fullPath) {
    this.id = id;
    this.modified = modified;
    this.created = created;
    this.author = author;
    this.views = views;
    this.forum = forum;
    this.subject = subject;
    this.urlName = urlName;
    this.fullPath = fullPath;
  }

  public Long getId() {
    return id;
  }

  public Date getModified() {
    return modified;
  }

  public Date getCreated() {
    return created;
  }

  public CompactUserBean getAuthor() {
    return author;
  }

  public Long getViews() {
    return views;
  }

  public CompactForumBean getForum() {
    return forum;
  }

  public String getSubject() {
    return subject;
  }

  public String getUrlName() {
    return urlName;
  }

  public String getFullPath() {
    return fullPath;
  }
  
  public static CompleteForumTopicBean fromEntity(ForumTopic entity) {
    if (entity == null)
      return null;
    
    return new CompleteForumTopicBean(entity.getId(), entity.getModified(), entity.getCreated(), CompactUserBean.fromEntity(entity.getAuthor()), entity.getViews(), CompactForumBean.fromEntity(entity.getForum()), entity.getSubject(), entity.getUrlName(), entity.getFullPath());
  }

  public static List<CompleteForumTopicBean> fromEntities(List<ForumTopic> entities) {
    List<CompleteForumTopicBean> beans = new ArrayList<CompleteForumTopicBean>(entities.size());

    for (ForumTopic entity : entities) {
      beans.add(fromEntity(entity));
    }

    return beans;
  }

  private Long id;

  private Date modified;

  private Date created;

  private CompactUserBean author;

  private Long views;

  private CompactForumBean forum;

  private String subject;

  private String urlName;

  private String fullPath;
}

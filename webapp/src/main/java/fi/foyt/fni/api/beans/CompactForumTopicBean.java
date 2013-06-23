package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.forum.ForumTopic;

public class CompactForumTopicBean {

	public CompactForumTopicBean(Long id, Date modified, Date created, Long authorId, Long views, Long forumId, String subject, String urlName) {
	  super();
	  this.id = id;
	  this.modified = modified;
	  this.created = created;
	  this.authorId = authorId;
	  this.views = views;
	  this.forumId = forumId;
	  this.subject = subject;
	  this.urlName = urlName;
  }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getForumId() {
	  return forumId;
  }
	
	public void setForumId(Long forumId) {
	  this.forumId = forumId;
  }

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
	
	public static CompactForumTopicBean fromEntity(ForumTopic entity) {
		if (entity == null)
			return null;
		
		return new CompactForumTopicBean(entity.getId(), entity.getModified(), entity.getCreated(), entity.getAuthor().getId(), entity.getViews(), entity.getForum().getId(), entity.getSubject(), entity.getUrlName());
	}

	public static List<CompactForumTopicBean> fromEntities(List<ForumTopic> entities) {
		List<CompactForumTopicBean> beans = new ArrayList<CompactForumTopicBean>(entities.size());

		for (ForumTopic entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	private Long id;

	private Date modified;

	private Date created;

	private Long authorId;

	private Long views;

	private Long forumId;

	private String subject;

	private String urlName;
}

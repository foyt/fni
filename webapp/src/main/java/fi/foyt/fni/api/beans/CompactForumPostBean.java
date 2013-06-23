package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.forum.ForumPost;

public class CompactForumPostBean {

	public CompactForumPostBean(Long id, Date modified, Date created, Long authorId, Long views, CompactForumTopicBean topic) {
		super();
		this.id = id;
		this.modified = modified;
		this.created = created;
		this.authorId = authorId;
		this.views = views;
		this.topic = topic;
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

	public CompactForumTopicBean getTopic() {
	  return topic;
  }
	
	public void setTopic(CompactForumTopicBean topic) {
	  this.topic = topic;
  }

	public static CompactForumPostBean fromEntity(ForumPost entity) {
		if (entity == null)
			return null;
		
		return new CompactForumPostBean(entity.getId(), entity.getModified(), entity.getCreated(), entity.getAuthor().getId(), entity.getViews(), CompactForumTopicBean.fromEntity(entity.getTopic()));
	}

	public static List<CompactForumPostBean> fromEntities(List<ForumPost> entities) {
		List<CompactForumPostBean> beans = new ArrayList<CompactForumPostBean>(entities.size());

		for (ForumPost entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	private Long id;

	private Date modified;

	private Date created;

	private Long authorId;

	private Long views;

	private CompactForumTopicBean topic;
}

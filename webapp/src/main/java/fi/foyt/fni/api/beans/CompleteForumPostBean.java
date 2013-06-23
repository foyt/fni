package fi.foyt.fni.api.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.foyt.fni.persistence.model.forum.ForumPost;

public class CompleteForumPostBean {

	public CompleteForumPostBean(Long id, Date modified, Date created, CompactUserBean author, Long views, CompactForumTopicBean topic) {
		super();
		this.id = id;
		this.modified = modified;
		this.created = created;
		this.author = author;
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

	public CompactUserBean getAuthor() {
	  return author;
  }
	
	public void setAuthor(CompactUserBean author) {
	  this.author = author;
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

	public static CompleteForumPostBean fromEntity(ForumPost entity) {
		if (entity == null)
			return null;
		
		return new CompleteForumPostBean(entity.getId(), entity.getModified(), entity.getCreated(), CompactUserBean.fromEntity(entity.getAuthor()), entity.getViews(), CompactForumTopicBean.fromEntity(entity.getTopic()));
	}

	public static List<CompleteForumPostBean> fromEntities(List<ForumPost> entities) {
		List<CompleteForumPostBean> beans = new ArrayList<CompleteForumPostBean>(entities.size());

		for (ForumPost entity : entities) {
			beans.add(fromEntity(entity));
		}

		return beans;
	}

	private Long id;

	private Date modified;

	private Date created;

	private CompactUserBean author;

	private Long views;

	private CompactForumTopicBean topic;
}

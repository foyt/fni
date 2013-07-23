package fi.foyt.fni.view.forum;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
	  @URLMapping(
			id = "forum-topic", 
			pattern = "/forum/#{forumTopicBackingBean.forumUrlName}/#{forumTopicBackingBean.topicUrlName}", 
			viewId = "/forum/topic.jsf"
	  )
	})
public class ForumTopicBackingBean {
	
	@URLAction
	public void load() {
		forum = forumController.findForumByUrlName(getForumUrlName());
		topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
		posts = forumController.listPostsByTopic(topic);
	}
	
	@Inject
	private ForumController forumController;
	
	public Forum getForum() {
		return forum;
	}
	
	public ForumTopic getTopic() {
		return topic;
	}
	
	public String getForumUrlName() {
		return forumUrlName;
	}
	
	public void setForumUrlName(String forumUrlName) {
		this.forumUrlName = forumUrlName;
	}
	
	public String getTopicUrlName() {
		return topicUrlName;
	}
	
	public void setTopicUrlName(String topicUrlName) {
		this.topicUrlName = topicUrlName;
	}
	
	public List<ForumPost> getPosts() {
		return posts;
	}
	
	public void setPosts(List<ForumPost> posts) {
		this.posts = posts;
	}
	
	public Long getAuthorPostCount(User author) {
		return forumController.countPostsByAuthor(author);
	}
	
	private Forum forum;
	private ForumTopic topic;
	private String forumUrlName;
	private String topicUrlName;
	private List<ForumPost> posts;
}

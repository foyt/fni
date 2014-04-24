package fi.foyt.fni.view.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;

@RequestScoped
@Stateful
@Named
@Join (path = "/forum/", to = "/forum/index.jsf")
public class ForumIndexBackingBean {
	
	private final static int MAX_TOPICS = 3;
	
	@Inject
	private ForumController forumController;
	
	/* ForumTopic */
	
	public List<ForumTopic> getTopics(Forum forum) {
		return forumController.listLatestForumTopicsByForum(forum, MAX_TOPICS);
	}
	
	public Long getTopicPostCount(ForumTopic topic) {
		return forumController.countPostsByTopic(topic);
	}
	
	public Date getTopicLastMessageDate(ForumTopic topic) {
		ForumPost post = forumController.getLastPostByTopic(topic);
		if (post != null) {
			return post.getCreated();
		}
		
		return null;
	}
	
	/* Forum */
	
	public List<Forum> getForums() {
		List<Forum> result = new ArrayList<>();
		
		for (ForumCategory forumCategory : forumController.listForumCategories()) {
			result.addAll(forumController.listForumsByCategory(forumCategory));
		}
		
		return result;
	}
	
	public Long getForumPostCount(Forum forum) {
		return forumController.countPostsByForum(forum);
	}

	public Long getForumTopicCount(Forum forum) {
		return forumController.countTopicsByForum(forum);
	}
	
	public Date getForumLastMessageDate(Forum forum) {
		ForumPost post = forumController.getLastPostByForum(forum);
		if (post != null) {
			return post.getCreated();
		}
		
		return null;
	}
}

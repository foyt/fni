package fi.foyt.fni.view.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Stateful
@Named
@Join (path = "/forum/", to = "/forum/index.jsf")
public class ForumIndexBackingBean {
	
	private final static int MAX_TOPICS = 3;
	
  @Inject
	private SessionController sessionController;
	
	@Inject
	private ForumController forumController;
	
	@RequestAction
	public String init() {
	  forums = new ArrayList<>();
    
    for (ForumCategory forumCategory : forumController.listVisibleCategories()) {
      forums.addAll(forumController.listForumsByCategory(forumCategory));
    }
    
    if (sessionController.isLoggedIn()) {
      // If user has never been in forum, we mark everything read
      User loggedUser = sessionController.getLoggedUser();
      if (!forumController.hasReadAnyForums(loggedUser)) {
        forumController.markAllForumsRead(loggedUser);
      }
    }
    
    return null;
	}
	
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
		return forums;
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
	
	private List<Forum> forums;
}

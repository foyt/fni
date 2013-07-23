package fi.foyt.fni.view.forum;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "forum-index", 
		pattern = "/forum/", 
		viewId = "/forum/index.jsf"
  )
})
public class ForumIndexBackingBean {
	
	@Inject
	private ForumController forumController;
	
	/* ForumCategories */

	public List<ForumCategory> getCategories() {
		return forumController.listForumCategories();
	}

	/* ForumTopic */
	
	public List<ForumTopic> getTopics(Forum forum) {
		return forumController.listTopicsByForum(forum);
	}
	
	/* Forum */
	
	public List<Forum> getForums(ForumCategory category) {
		return forumController.listForumsByCategory(category);
	}
	
	public Long getForumPostCount(Forum forum) {
		return forumController.countPostsByForum(forum);
	}

	public Long getForumTopicCount(Forum forum) {
		return forumController.countTopicsByForum(forum);
	}
	
	public Date getLastMessageDate(Forum forum) {
		ForumPost post = forumController.getLastPostByForum(forum);
		if (post != null) {
			return post.getCreated();
		}
		
		return null;
	}
}

package fi.foyt.fni.view.forum;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "forum", 
		pattern = "/forum/#{forumBackingBean.forumUrlName}", 
		viewId = "/forum/forum.jsf"
  )
})
public class ForumBackingBean {
	
	@Inject
	private ForumController forumController;

	@URLAction
	public void load() {
		forum = forumController.findForumByUrlName(getForumUrlName());
	}
	
	public List<ForumTopic> getTopics() {
		List<ForumTopic> topics = forumController.listTopicsByForum(getForum());
		Collections.sort(topics, new Comparator<ForumTopic>() {
			@Override
			public int compare(ForumTopic o1, ForumTopic o2) {
				return o2.getCreated().compareTo(o1.getCreated());
			}
		});
		
		return topics;
	}
	
	public Forum getForum() {
		return forum;
	}
	
	public String getForumUrlName() {
		return forumUrlName;
	}
	
	public void setForumUrlName(String forumUrlName) {
		this.forumUrlName = forumUrlName;
	}
	
	public Long getTopicPostCount(ForumTopic topic) {
		return forumController.countPostsByTopic(topic);
	}
	
	public Date getLastMessageDate(ForumTopic topic) {
		ForumPost post = forumController.getLastPostByTopic(topic);
		if (post != null) {
			return post.getCreated();
		}
		
		return null;
	}
	
	private Forum forum;
	private String forumUrlName;
}

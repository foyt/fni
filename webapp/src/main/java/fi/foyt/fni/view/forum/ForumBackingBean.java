package fi.foyt.fni.view.forum;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.session.SessionController;

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

	@Inject
	private SessionController sessionController;

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
	
	public String getNewTopicSubject() {
		return newTopicSubject;
	}
	
	public void setNewTopicSubject(String newTopicSubject) {
		this.newTopicSubject = newTopicSubject;
	}
	
	public String getNewTopicContents() {
		return newTopicContents;
	}
	
	public void setNewTopicContents(String newTopicContents) {
		this.newTopicContents = newTopicContents;
	}
	
	@LoggedIn
	@Secure (Permission.FORUM_TOPIC_CREATE)
	@SecurityContext (context = "#{forumBackingBean.forum}")
	public void newTopic() throws IOException {
		User author = sessionController.getLoggedUser();
		ForumTopic topic = forumController.createTopic(getForum(), getNewTopicSubject(), author);
		ForumPost post = forumController.createForumPost(topic, author, getNewTopicContents());
		
		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
		  .append("/forum/")
		  .append(forum.getUrlName())
		  .append('/')
		  .append(topic.getUrlName())
		  .append("#p")
		  .append(post.getId())
		  .toString());
	}
	
	private Forum forum;
	private String forumUrlName;
	private String newTopicSubject;
	private String newTopicContents;
}

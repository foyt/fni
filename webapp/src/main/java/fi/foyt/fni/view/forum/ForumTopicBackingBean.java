package fi.foyt.fni.view.forum;

import java.io.IOException;
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
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

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
	
	@Inject
	private UserController userController;
	
	@Inject
	private ForumController forumController;

	@Inject
	private SessionController sessionController;
	
	@URLAction
	public void load() {
		forum = forumController.findForumByUrlName(getForumUrlName());
		topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
		posts = forumController.listPostsByTopic(topic);
	}
	
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
	
	public boolean getAuthorHasImage(User author) {
		return userController.hasProfileImage(author);
	}
	
	public String getReply() {
		return reply;
	}
	
	public void setReply(String reply) {
		this.reply = reply;
	}

	@LoggedIn
	public void postReply() throws IOException {
		User author = sessionController.getLoggedUser();
		ForumPost post = forumController.createForumPost(getTopic(), author, getReply());

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
	private ForumTopic topic;
	private String forumUrlName;
	private String topicUrlName;
	private List<ForumPost> posts;
	private String reply;
}

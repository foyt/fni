package fi.foyt.fni.view.forum;

import java.io.FileNotFoundException;
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

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.jsf.NavigationController;
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
@Join (path = "/forum/{forumUrlName}", to = "/forum/forum.jsf")
public class ForumBackingBean {

  @Parameter
  @Matches ("[a-z0-9_.-]{1,}")
  private String forumUrlName;

	@Inject
	private ForumController forumController;

	@Inject
	private SessionController sessionController;

  @Inject
  private NavigationController navigationController;

	@RequestAction
	public String load() throws FileNotFoundException {
		forum = forumController.findForumByUrlName(getForumUrlName());
		if (forum == null) {
		  return navigationController.notFound();
		}
		
		topics = forumController.listTopicsByForum(forum);
		Collections.sort(topics, new Comparator<ForumTopic>() {
			@Override
			public int compare(ForumTopic o1, ForumTopic o2) {
				return o2.getCreated().compareTo(o1.getCreated());
			}
		});
		
		return null;
	}
	
	public List<ForumTopic> getTopics() {
		return topics;
	}
	
	public int getTopicCount() {
		return getTopics().size();
	}
	
	public Date getLastMessageDate() {
		ForumPost post = forumController.getLastPostByForum(forum);
		if (post != null) {
			return post.getCreated();
		}
		
		return null;
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
	
	public Date getTopicLastMessageDate(ForumTopic topic) {
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
	
	public boolean getAllowTopicCreation() {
		return forum.getAllowTopicCreation();
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
	private String newTopicSubject;
	private String newTopicContents;
	private List<ForumTopic> topics;
}

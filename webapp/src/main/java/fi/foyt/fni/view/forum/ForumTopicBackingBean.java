package fi.foyt.fni.view.forum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import com.ocpsoft.pretty.faces.annotation.URLQueryParameter;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
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
	
	private static final int POST_PER_PAGE = 3;
	
	@Inject
	private UserController userController;
	
	@Inject
	private ForumController forumController;

	@Inject
	private SessionController sessionController;
	
	@URLAction
	public void load() {
		if (page == null) {
			page = 0;
		}
		
		forum = forumController.findForumByUrlName(getForumUrlName());
		topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
		
		Long postCount = forumController.countPostsByTopic(topic);
		Integer pageCount = postCount.intValue() / POST_PER_PAGE;
		if ((pageCount * POST_PER_PAGE) < postCount) {
			pageCount++;
		}
		
		posts = forumController.listPostsByTopic(topic, page * POST_PER_PAGE, POST_PER_PAGE);
		
		pages = new ArrayList<>();
		for (int i = 0; i < pageCount; i++) {
			pages.add(i);
		}
	}
	
	public Integer getPage() {
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}
	
	public List<Integer> getPages() {
		return pages;
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
	
	public String getReply() {
		return reply;
	}
	
	public void setReply(String reply) {
		this.reply = reply;
	}

	@LoggedIn
	@Secure (Permission.FORUM_POST_CREATE)
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
	
	@URLQueryParameter("page")
	private Integer page;
	private List<Integer> pages;
	private Forum forum;
	private ForumTopic topic;
	private String forumUrlName;
	private String topicUrlName;
	private List<ForumPost> posts;
	private String reply;
}

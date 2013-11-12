package fi.foyt.fni.view.forum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

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
import fi.foyt.fni.utils.faces.FacesUtils;

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
	
	public static final int POST_PER_PAGE = 3;
	
	@Inject
	private UserController userController;
	
	@Inject
	private ForumController forumController;

	@Inject
	private SessionController sessionController;
	
	@URLAction
	public void load() throws FileNotFoundException {
		if (page == null) {
			page = 0;
		}
		
		forum = forumController.findForumByUrlName(getForumUrlName());
		if (forum == null) {
		  throw new FileNotFoundException();
		}
		
		ForumTopic topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
    if (topic == null) {
      throw new FileNotFoundException();
    }
    
    topicId = topic.getId();
    topicSubject = topic.getSubject();
    topicAuthorId = topic.getAuthor().getId();
    topicAuthorName = topic.getAuthor().getFullName();
    topicCreated = topic.getCreated();
    
    long pageCount = getPostCount();
		
		posts = forumController.listPostsByTopic(topic, page * POST_PER_PAGE, POST_PER_PAGE);
		
		pages = new ArrayList<>();
		for (int i = 0; i < pageCount; i++) {
			pages.add(i);
		}

		forumController.updateTopicViews(topic, topic.getViews() + 1);
		for (ForumPost post : posts) {
		  forumController.updatePostViews(post, post.getViews() + 1);
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
	
	public Long getTopicId() {
    return topicId;
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

	public Long getTopicAuthorId() {
    return topicAuthorId;
  }
	
	public String getTopicAuthorName() {
    return topicAuthorName;
  }
	
	public String getTopicSubject() {
    return topicSubject;
  }
	
	public Date getTopicCreated() {
    return topicCreated;
  }
	
	@LoggedIn
	@Secure (Permission.FORUM_POST_CREATE)
	public void postReply() throws IOException {
		String content = StringUtils.strip(getReply());
		if (StringUtils.isEmpty(content)) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("forum.topic.contentRequired"));
		} else {
		  ForumTopic topic = forumController.findForumTopicById(getTopicId());
  		User author = sessionController.getLoggedUser();
  		ForumPost post = forumController.createForumPost(topic, author, content);
  
  		FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
  		  .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
  		  .append("/forum/")
  		  .append(forum.getUrlName())
  		  .append('/')
  		  .append(topic.getUrlName())
  		  .append("?page=")
  		  .append(getPostCount() - 1)
  		  .append("#p")
  		  .append(post.getId())
  		  .toString());
		}
	}
	
	public boolean getWatchingTopic() {
	  if (!sessionController.isLoggedIn()) {
	    return false;
	  }
	  
	  ForumTopic topic = forumController.findForumTopicById(getTopicId());
	  return forumController.isWatchingTopic(sessionController.getLoggedUser(), topic);
	}
	
	@LoggedIn
  public String watchTopic() {
	  ForumTopic topic = forumController.findForumTopicById(getTopicId());
    forumController.addTopicWatcher(sessionController.getLoggedUser(), topic);
    return "pretty:forum-topic";
  }
	
	@LoggedIn
  public String stopWatchingTopic() {
    ForumTopic topic = forumController.findForumTopicById(getTopicId());
    forumController.removeTopicWatcher(sessionController.getLoggedUser(), topic);
    return "pretty:forum-topic";
  }
	
	private long getPostCount() {
	  ForumTopic topic = forumController.findForumTopicById(getTopicId());
		return Math.round(Math.ceil(new Double(forumController.countPostsByTopic(topic)) / POST_PER_PAGE));
	}
	
	@URLQueryParameter("page")
	private Integer page;
	private List<Integer> pages;
	private Forum forum;
	private Long topicId;
	private String topicSubject;
	private String topicAuthorName;
	private Long topicAuthorId;
	private Date topicCreated;
	private String forumUrlName;
	private String topicUrlName;
	private List<ForumPost> posts;
	private String reply;
}

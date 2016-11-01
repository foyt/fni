package fi.foyt.fni.view.forum;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
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
import fi.foyt.fni.security.SecurityController;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.faces.FacesUtils;

@RequestScoped
@Stateful
@Named
@Join (path = "/forum/{forumUrlName}/{topicUrlName}", to = "/forum/topic.jsf")
@SuppressWarnings("squid:S3306")
public class ForumTopicBackingBean {
  
  public static final int POST_PER_PAGE = 15;
  
  @Parameter
  @Matches ("[a-z0-9_.-]{1,}")
  private String forumUrlName;
  
  @Parameter
  @Matches ("[a-z0-9_.-]{1,}")
  private String topicUrlName;
  
  @Parameter
  private Integer page;

  @Inject
  private Logger logger;

	@Inject
	private ForumController forumController;

	@Inject
	private SessionController sessionController;
	
	@Inject
	private SecurityController securityController;

  @Inject
  private NavigationController navigationController;
  
  private List<Integer> pages;
  private Forum forum;
  private Long topicId;
  private String topicSubject;
  private String topicAuthorName;
  private Long topicAuthorId;
  private Date topicCreated;
  private List<ForumPost> posts;
  private String reply;
  private long postCount;
	
	@RequestAction
	public String load() {
	  if (page == null) {
			page = 0;
		}
	  
	  page = Math.max(page, 0);
		
	  forum = forumController.findForumByUrlName(getForumUrlName());
		if (forum == null) {
		  return navigationController.notFound();
		}   

		if (!forum.getCategory().getVisible()) {
      return navigationController.notFound();
    }
    
		ForumTopic topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
    if (topic == null) {
      return navigationController.notFound();
    }
    
    topicId = topic.getId();
    topicSubject = topic.getSubject();
    topicAuthorId = topic.getAuthor().getId();
    topicAuthorName = topic.getAuthor().getFullName();
    topicCreated = topic.getCreated();
    postCount = Math.round(Math.ceil((double) forumController.countPostsByTopic(topic)) / POST_PER_PAGE);

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

    if (sessionController.isLoggedIn()) {
      // If user has never been in forum, we mark everything read
      User loggedUser = sessionController.getLoggedUser();
      if (!forumController.hasReadAnyForums(loggedUser)) {
        forumController.markAllForumTopicsRead(loggedUser);
      } else {
        forumController.markForumTopicRead(topic, loggedUser); 
      }
    }

		return null;
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
	
	public boolean getMayModifyPost(ForumPost forumPost) {
	  try {
      return securityController.checkPermission(Permission.FORUM_POST_MODIFY, forumPost.getId());
    } catch (FileNotFoundException e) {
      return false;
    }
	}
	
	public String postReply() {
	  if (!sessionController.isLoggedIn()) {
	    return navigationController.requireLogin();
	  }
    
	  if (!sessionController.hasLoggedUserPermission(Permission.FORUM_POST_CREATE)) {
	    return navigationController.accessDenied();
	  }
       
		String content = StringUtils.strip(getReply());
		if (StringUtils.isEmpty(content)) {
			FacesUtils.addMessage(FacesMessage.SEVERITY_WARN, FacesUtils.getLocalizedValue("forum.topic.contentRequired"));
			return null;
		} else {
		  ForumTopic topic = forumController.findForumTopicById(getTopicId());
  		User author = sessionController.getLoggedUser();
  		ForumPost post = forumController.createForumPost(topic, author, content);
  		Long pageCount = Math.round(Math.ceil(((double) getPostCount() + 1) / POST_PER_PAGE));
  		
  		try {
        FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
          .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
          .append("/forum/")
          .append(forum.getUrlName())
          .append('/')
          .append(topic.getUrlName())
          .append("?page=")
          .append(pageCount - 1)
          .append("#p")
          .append(post.getId())
          .toString());
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Failed to redirect user to new post", e);
      }
  		
      return null;
		}
	}
	
	public boolean getWatchingTopic() {
	  if (!sessionController.isLoggedIn()) {
	    return false;
	  }
	  
	  ForumTopic topic = forumController.findForumTopicById(getTopicId());
	  return forumController.isWatchingTopic(sessionController.getLoggedUser(), topic);
	}
	
	public String watchTopic() {
	  if (!sessionController.isLoggedIn()) {
      return navigationController.requireLogin();
    }
	  
	  ForumTopic topic = forumController.findForumTopicById(getTopicId());
    forumController.addTopicWatcher(sessionController.getLoggedUser(), topic);
    return "pretty:forum-topic";
  }
	
	public String stopWatchingTopic() {
	  if (!sessionController.isLoggedIn()) {
      return navigationController.requireLogin();
    }
  
    ForumTopic topic = forumController.findForumTopicById(getTopicId());
    forumController.removeTopicWatcher(sessionController.getLoggedUser(), topic);
    return "pretty:forum-topic";
  }
	
	private long getPostCount() {
		return postCount;
	}
}

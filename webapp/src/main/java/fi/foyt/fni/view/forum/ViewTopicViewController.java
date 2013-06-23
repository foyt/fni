package fi.foyt.fni.view.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserRole;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.utils.auth.AuthUtils;
import fi.foyt.fni.view.NotFoundException;
import fi.foyt.fni.view.PageViewController;
import fi.foyt.fni.view.ViewControllerContext;

@RequestScoped
@Stateful
public class ViewTopicViewController extends PageViewController {

  @Inject
  private SessionController sessionController;

  @Inject
	private ForumController forumController;

  @Override
  public boolean checkPermissions(ViewControllerContext context) {
    return true;
  }

  @Override
  public void execute(ViewControllerContext context) {
  	super.execute(context);
 
  	String forumUrlName = context.getRequest().getParameter("forumUrlName");
  	String topicUrlName = context.getRequest().getParameter("topicUrlName");

    ForumTopic topic = forumController.findForumTopicByUrlNames(forumUrlName, topicUrlName);
    if (topic == null) {
    	throw new NotFoundException();
    } 
    
  	forumController.updateTopicViews(topic, topic.getViews() + 1);

    List<ForumPost> forumPosts = forumController.listPostsByTopic(topic);
    List<ForumPostBean> forumPostBeans = new ArrayList<ViewTopicViewController.ForumPostBean>(forumPosts.size());
    for (ForumPost forumPost : forumPosts) {
      User postAuthor = forumPost.getAuthor();
      String authorName = postAuthor.getNickname();
      if (StringUtils.isBlank(authorName)) {
        authorName = postAuthor.getFullName();
      }
      
      Long authorPostCount = forumController.countPostsByAuthor(postAuthor);
      
      forumPostBeans.add(new ForumPostBean(forumPost.getId(), authorName, authorPostCount, forumPost.getAuthor().getProfileImage() != null, forumPost.getAuthor().getId(), forumPost.getContent(), forumPost.getCreated(), forumPost.getModified()));
    
      forumController.updatePostViews(forumPost, forumPost.getViews() + 1);
    }
    
    User loggedUser = sessionController.getLoggedUser();

    context.getRequest().setAttribute("canCreatePost", AuthUtils.getInstance().isAllowed(loggedUser, UserRole.USER));
    context.getRequest().setAttribute("topic", topic);
    context.getRequest().setAttribute("posts", forumPostBeans);
    context.setIncludeJSP("/jsp/forum/viewtopic.jsp");

  }
  
  public class ForumPostBean {

    public ForumPostBean(Long id, String authorName, Long authorPostCount, boolean hasAuthorImage, Long authorId, String content, Date created, Date modified) {
      this.id = id;
      this.authorName = authorName;
      this.authorPostCount = authorPostCount;
      this.content = content;
      this.created = created;
      this.modified = modified;
      this.hasAuthorImage = hasAuthorImage;
      this.authorId = authorId;
    }

    public Long getId() {
      return id;
    }

    public String getAuthorName() {
      return authorName;
    }

    public Long getAuthorPostCount() {
      return authorPostCount;
    }
    
    public boolean getHasAuthorImage() {
      return hasAuthorImage;
    }
    
    public Long getAuthorId() {
      return authorId;
    }
    
    public String getContent() {
      return content;
    }

    public Date getCreated() {
      return created;
    }

    public Date getModified() {
      return modified;
    }

    private Long id;
    private String authorName;
    private Long authorPostCount;
    private boolean hasAuthorImage;
    private Long authorId;
    private String content;
    private Date created;
    private Date modified;
  }

}
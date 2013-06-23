package fi.foyt.fni.view.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.persistence.model.forum.Forum;
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
public class ViewForumViewController extends PageViewController {

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
  	
  	String urlName = context.getRequest().getParameter("forumUrlName");

    Forum forum = forumController.findForumByUrlName(urlName);
    if (forum == null) {
    	throw new NotFoundException();
    } 
    
    List<ForumTopic> forumTopics = forumController.listTopicsByForum(forum);
    
    List<ForumTopicBean> forumTopicBeans = new ArrayList<ForumTopicBean>();
    for (ForumTopic forumTopic : forumTopics) {
      User author = forumTopic.getAuthor(); 
      String authorName = author.getNickname();
      if (StringUtils.isBlank(authorName)) {
        authorName = author.getFullName();
      }
      
      Long replyCount = forumController.countTopicReplies(forumTopic);
      Long viewCount = forumTopic.getViews();
      ForumPost lastTopicPost = forumController.findLastTopicPost(forumTopic);
      Date lastPost = lastTopicPost != null ? lastTopicPost.getCreated() : null;
      
      forumTopicBeans.add(new ForumTopicBean(forumTopic.getId(), forumTopic.getUrlName(), forumTopic.getSubject(), authorName, replyCount, viewCount, lastPost)); 
    }
    
    User loggedUser = sessionController.getLoggedUser();
   
    context.getRequest().setAttribute("canCreateTopic", AuthUtils.getInstance().isAllowed(loggedUser, UserRole.USER));
    context.getRequest().setAttribute("forum", forum);
    context.getRequest().setAttribute("topics", forumTopicBeans);
    context.setIncludeJSP("/jsp/forum/viewforum.jsp");
  }

  public class ForumTopicBean {

    public ForumTopicBean(Long id, String urlName, String subject, String authorName, Long replyCount, Long viewCount, Date lastPost) {
      this.id = id;
      this.urlName = urlName;
      this.subject = subject;
      this.authorName = authorName;
      this.replyCount = replyCount;
      this.viewCount = viewCount;
      this.lastPost = lastPost;
    }

    public Long getId() {
      return id;
    }
    
    public String getSubject() {
      return subject;
    }
    
    public String getUrlName() {
      return urlName;
    }

    public String getAuthorName() {
      return authorName;
    }

    public Long getReplyCount() {
      return replyCount;
    }

    public Long getViewCount() {
      return viewCount;
    }

    public Date getLastPost() {
      return lastPost;
    }

    private Long id;
    private String urlName;
    private String subject;
    private String authorName;
    private Long replyCount;
    private Long viewCount;
    private Date lastPost;
  }

}
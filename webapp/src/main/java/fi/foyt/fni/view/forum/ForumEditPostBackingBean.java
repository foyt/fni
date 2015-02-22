package fi.foyt.fni.view.forum;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.faces.annotation.Deferred;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.Permission;
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;

@RequestScoped
@Stateful
@Named
@Join (path = "/forum/{forumUrlName}/{topicUrlName}/edit/{postId}", to = "/forum/editpost.jsf")
@LoggedIn
public class ForumEditPostBackingBean {
  
  @Parameter
  @Matches ("[a-z0-9_.-]{1,}")
  private String forumUrlName;
  
  @Parameter
  @Matches ("[a-z0-9_.-]{1,}")
  private String topicUrlName;

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long postId;

  @Inject
  private ForumController forumController;

  @Inject
  private NavigationController navigationController;
  
  @RequestAction
  @Secure (Permission.FORUM_POST_MODIFY)
  @SecurityContext (context = "#{forumEditPostBackingBean.postId}")
  @Deferred
  public String init() throws FileNotFoundException {
    forum = forumController.findForumByUrlName(getForumUrlName());
    if (forum == null) {
      return navigationController.notFound();
    }
    
    topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
    if (topic == null) {
      return navigationController.notFound();
    }
    
    ForumPost forumPost = forumController.findForumPostById(postId);
    if ((forumPost == null) || !forumPost.getTopic().getId().equals(topic.getId())) {
      return navigationController.notFound();
    }
    
    if (!forumPost.getTopic().getForum().getId().equals(forum.getId())) {
      return navigationController.notFound();
    }
    
    postContent = forumPost.getContent();
    
    return null;
  }
  
  public String getForumUrlName() {
    return forumUrlName;
  }
  
  public void setForumUrlName(String forumUrlName) {
    this.forumUrlName = forumUrlName;
  }
  
  public Long getPostId() {
    return postId;
  }
  
  public void setPostId(Long postId) {
    this.postId = postId;
  }
  
  public String getTopicUrlName() {
    return topicUrlName;
  }
  
  public void setTopicUrlName(String topicUrlName) {
    this.topicUrlName = topicUrlName;
  }
  
  public Forum getForum() {
    return forum;
  }
  
  public ForumTopic getTopic() {
    return topic;
  }
  
  public String getPostContent() {
    return postContent;
  }
  
  public void setPostContent(String postContent) {
    this.postContent = postContent;
  }
  
  public void save() throws IOException {
    ForumPost forumPost = forumController.findForumPostById(postId);
    forumController.updateForumPostContent(forumPost, getPostContent());
    long page = Math.round(Math.floor(forumController.getIndexOfPostInTopic(forumPost) / ForumTopicBackingBean.POST_PER_PAGE));

    FacesContext.getCurrentInstance().getExternalContext().redirect(new StringBuilder()
      .append(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath())
      .append("/forum/")
      .append(forum.getUrlName())
      .append('/')
      .append(topic.getUrlName())
      .append("?page=")
      .append(page)
      .append("#p")
      .append(forumPost.getId())
      .toString());
  }
  
  private Forum forum;
  private ForumTopic topic;
  private String postContent;
}

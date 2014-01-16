package fi.foyt.fni.view.forum;

import java.io.FileNotFoundException;
import java.io.IOException;

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
import fi.foyt.fni.security.LoggedIn;
import fi.foyt.fni.security.Secure;
import fi.foyt.fni.security.SecurityContext;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "forum-modify-post", 
		pattern = "/forum/#{forumEditPostBackingBean.forumUrlName}/#{forumEditPostBackingBean.topicUrlName}/edit/#{forumEditPostBackingBean.postId}", 
		viewId = "/forum/editpost.jsf"
  )
})
public class ForumEditPostBackingBean {

  @Inject
  private ForumController forumController;
  
  @URLAction
  @Secure (Permission.FORUM_POST_MODIFY)
  public void init() throws FileNotFoundException {
    forum = forumController.findForumByUrlName(getForumUrlName());
    if (forum == null) {
      throw new FileNotFoundException();
    }
    
    topic = forumController.findForumTopicByForumAndUrlName(forum, topicUrlName);
    if (topic == null) {
      throw new FileNotFoundException();
    }
    
    ForumPost forumPost = forumController.findForumPostById(postId);
    
    if (!forumPost.getTopic().getId().equals(topic.getId())) {
      throw new FileNotFoundException();
    }
    
    if (!forumPost.getTopic().getForum().getId().equals(forum.getId())) {
      throw new FileNotFoundException();
    }
    
    postContent = forumPost.getContent();
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
  
  @LoggedIn
  @Secure (Permission.FORUM_POST_MODIFY)
  @SecurityContext (context = "#{forumEditPostBackingBean.postId}")
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
  
  private String forumUrlName;
  private String topicUrlName;
  private Long postId;
  private Forum forum;
  private ForumTopic topic;
  private String postContent;
}

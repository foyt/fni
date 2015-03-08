package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionEventPageController;
import fi.foyt.fni.illusion.IllusionEventPageVisibility;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/event-forum/{postId}", to = "/illusion/event-forum-post.jsf")
public class IllusionEventForumPostBackingBean extends AbstractIllusionEventBackingBean {

  @Parameter
  private String urlName;

  @Parameter
  @Matches ("[0-9]{1,}")
  private Long postId;
  
  @Inject
  private Logger logger;

  @Inject
  private ForumController forumController;

  @Inject
  private UserController userController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventPageController illusionEventPageController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private JadeController jadeController;

  @Inject
  private SessionController sessionController;

  @Inject
  private NavigationController navigationController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.FORUM);
    illusionEventNavigationController.setEventUrlName(getUrlName());
    
    if (!illusionEvent.getPublished()) {
      if (participant == null) {
        return navigationController.requireLogin(navigationController.accessDenied());
      }
      
      if (participant.getRole() != IllusionEventParticipantRole.ORGANIZER) {
        return navigationController.accessDenied();
      }
    }
    
    IllusionEventPageVisibility visibility = illusionEventPageController.getPageVisibility(illusionEvent, IllusionEventPage.Static.FORUM.toString());
    if (visibility == IllusionEventPageVisibility.HIDDEN) {
      return navigationController.accessDenied();
    }    
    
    if (visibility != IllusionEventPageVisibility.VISIBLE) {
      if (participant == null) {
        return navigationController.requireLogin(navigationController.accessDenied());
      }
    }

    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, IllusionEventPage.Static.FORUM);

    ForumTopic topic = illusionEvent.getForumTopic();
    if (topic == null) {
      return navigationController.internalError();
    }
    
    ForumPost post = forumController.findForumPostById(getPostId());
    if (post == null) {
      return navigationController.notFound();
    }
    
    if (!topic.getId().equals(post.getTopic().getId())) {
      return navigationController.notFound();
    }
    
    IllusionEventParticipant forumAuthorParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, post.getAuthor());
    String forumAuthorName = forumAuthorParticipant.getDisplayName();
    if (StringUtils.isBlank(forumAuthorName)) {
      forumAuthorName = userController.getUserDisplayName(post.getAuthor());
    }
    
    templateModelBuilder
      .put("topicId", topic.getId())
      .put("postId", post.getId())
      .put("postModified", post.getModified())
      .put("postCreated", post.getCreated())
      .put("postAuthorId", post.getAuthor().getId())
      .put("postAuthorParticipantId", forumAuthorParticipant.getId())
      .put("postAuthorName", forumAuthorName)
      .put("postContent", post.getContent());

    forumController.updatePostViews(post, post.getViews() + 1);

    try {
      Map<String, Object> templateModel = templateModelBuilder.build(sessionController.getLocale());
      html = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/forum-post", templateModel);
    } catch (JadeException | IOException e) {
      logger.log(Level.SEVERE, "Could not parse jade template", e);
      return navigationController.internalError();
    }

    return null;
  }

  @Override
  public String getUrlName() {
    return urlName;
  }

  public void setUrlName(@SecurityContext String urlName) {
    this.urlName = urlName;
  }
  
  public void setPostId(Long postId) {
    this.postId = postId;
  }
  
  public Long getPostId() {
    return postId;
  }
  
  public String getHtml() {
    return html;
  }

  private String html;
}

package fi.foyt.fni.view.illusion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;

import de.neuland.jade4j.exceptions.JadeException;
import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.illusion.IllusionEventPage;
import fi.foyt.fni.illusion.IllusionTemplateModelBuilderFactory.IllusionTemplateModelBuilder;
import fi.foyt.fni.jade.JadeController;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipant;
import fi.foyt.fni.security.SecurityContext;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
@Join(path = "/illusion/event/{urlName}/forum", to = "/illusion/event-forum.jsf")
public class IllusionEventForumBackingBean extends AbstractIllusionEventBackingBean {

  private static final int POST_PER_PAGE = 3;

  @Parameter
  private String urlName;
  
  @Parameter
  private Integer page;

  @Inject
  private Logger logger;

  @Inject
  private ForumController forumController;

  @Inject
  private IllusionEventController illusionEventController;

  @Inject
  private IllusionEventNavigationController illusionEventNavigationController;

  @Inject
  private JadeController jadeController;

  @Inject
  private SessionController sessionController;

  @Override
  public String init(IllusionEvent illusionEvent, IllusionEventParticipant participant) {
    illusionEventNavigationController.setSelectedPage(IllusionEventPage.Static.FORUM);
    illusionEventNavigationController.setEventUrlName(getUrlName());

    IllusionTemplateModelBuilder templateModelBuilder = createDefaultTemplateModelBuilder(illusionEvent, participant, IllusionEventPage.Static.FORUM);

    ForumTopic topic = illusionEvent.getForumTopic();
    if (topic == null) {
      return "/error/not-found.jsf";
    }

    int topicPage = page != null ? page : 0;
    int pageCount = (int) Math.round((float) Math.ceil(new Double(forumController.countPostsByTopic(topic)) / POST_PER_PAGE));
    int[] topicPages = new int[pageCount];
    for (int i = 0; i < pageCount; i++) {
      topicPages[i] = i;
    }
    
    IllusionEventParticipant topicAuthorParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(illusionEvent, topic.getAuthor());
    
    templateModelBuilder
      .put("topicId", topic.getId())
      .put("topicSubject", topic.getSubject())
      .put("topicAuthorId", topic.getAuthor().getId())
      .put("topicAuthorName", topic.getAuthor().getFullName())
      .put("topicAuthorParticipantId", topicAuthorParticipant != null ? topicAuthorParticipant.getId() : null)
      .put("topicCreated", topic.getCreated())
      .put("topicPages", topicPages)
      .put("topicPage", topicPage);
    
    templateModelBuilder
      .addLocale("illusion.forum.title")
      .addLocale("illusion.forum.authorPosts")
      .addLocale("illusion.forum.postReply");
    
    List<ForumPost> posts = forumController.listPostsByTopic(topic, topicPage * POST_PER_PAGE, POST_PER_PAGE);

    forumController.updateTopicViews(topic, topic.getViews() + 1);
    for (ForumPost post : posts) {
      forumController.updatePostViews(post, post.getViews() + 1);
    }

    templateModelBuilder.put("posts", toPostModel(illusionEvent, posts));
    
    try {
      Map<String, Object> templateModel = templateModelBuilder.build(sessionController.getLocale());
      headHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/forum-head", templateModel);
      contentsHtml = jadeController.renderTemplate(getJadeConfiguration(), illusionEvent.getUrlName() + "/forum-contents", templateModel);
    } catch (JadeException | IOException e) {
      logger.log(Level.SEVERE, "Could not parse jade template", e);
      return "/error/internal-error.jsf";
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
  
  public Integer getPage() {
    return page;
  }
  
  public void setPage(Integer page) {
    this.page = page;
  }
  
  public String getHeadHtml() {
    return headHtml;
  }

  public String getContentsHtml() {
    return contentsHtml;
  }
  
  private List<PostModel> toPostModel(IllusionEvent event, List<ForumPost> posts) {
    List<PostModel> result = new ArrayList<>();
    
    for (ForumPost post : posts) {
      result.add(toPostModel(event, post));
    }
    
    return result;
  }
  
  private PostModel toPostModel(IllusionEvent event, ForumPost post) {
    Long authorPosts = forumController.countPostsByAuthor(post.getAuthor());
    IllusionEventParticipant topicAuthorParticipant = illusionEventController.findIllusionEventParticipantByEventAndUser(event, post.getAuthor());
    
    return new PostModel(post.getId(), post.getModified(), post.getCreated(), post.getAuthor().getId(), 
        topicAuthorParticipant != null ? topicAuthorParticipant.getId() : null,
        post.getAuthor().getFullName(), authorPosts.intValue(), post.getContent());
  }

  private String headHtml;
  private String contentsHtml;

  public class PostModel {
    
    public PostModel(long id, Date modified, Date created, Long authorId, Long authorParticipantId, String authorName, int authorPosts, String content) {
      super();
      this.id = id;
      this.modified = modified;
      this.created = created;
      this.authorId = authorId;
      this.authorParticipantId = authorParticipantId;
      this.authorName = authorName;
      this.authorPosts = authorPosts;
      this.content = content;
    }

    public long getId() {
      return id;
    }

    public Date getModified() {
      return modified;
    }

    public Date getCreated() {
      return created;
    }

    public Long getAuthorId() {
      return authorId;
    }
    
    public Long getAuthorParticipantId() {
      return authorParticipantId;
    }

    public String getAuthorName() {
      return authorName;
    }
    
    public int getAuthorPosts() {
      return authorPosts;
    }

    public String getContent() {
      return content;
    }

    private long id;
    private Date modified;
    private Date created;
    private Long authorId;
    private Long authorParticipantId;
    private String authorName;
    private int authorPosts;
    private String content;
  }
}

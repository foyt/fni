package fi.foyt.fni.view.users;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.gamelibrary.SessionShoppingCartController;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

@SuppressWarnings("el-syntax")
@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "users-profile", 
		pattern = "/profile/#{ /[0-9]*/ usersProfileBackingBean.userId}", 
		viewId = "/users/profile.jsf"
  )
})
public class UsersProfileBackingBean {
	
	@Inject
	private UserController userController;

	@Inject
	private PublicationController publicationController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ForumController forumController;

	@Inject
	private SessionShoppingCartController sessionShoppingCartController;

	@URLAction
	public void init() throws FileNotFoundException {
		User user = userController.findUserById(getUserId());
		if (user == null) {
			throw new FileNotFoundException();
		}
		
		if (user.getArchived()) {
		  throw new FileNotFoundException();
		}
		
		StringBuilder fullNameBuilder = new StringBuilder();
		if (StringUtils.isNotBlank(user.getFullName())) {
			fullNameBuilder.append(user.getFullName());
		}
		
		if (!StringUtils.isBlank(user.getNickname())) {
		  if (fullNameBuilder.length() != 0) {
			  fullNameBuilder
			    .append(" (")
			    .append(user.getNickname())
			    .append(')');
		  } else {
			  fullNameBuilder
		      .append(user.getNickname());
		  }
		}
		
		this.fullName = fullNameBuilder.toString();
		this.about = user.getAbout();
		this.publishedPublications = publicationController.listPublishedPublicationsByAuthor(user);
		this.hasGameLibraryPublications = publishedPublications.size() > 0;
		
		forumTotalPosts = forumController.countPostsByAuthor(user);
		if (forumTotalPosts > 0) {
			ForumPost lastPost = forumController.findLastPostByAuthor(user);
			if (lastPost != null) {
				forumLastMessageTopic = lastPost.getTopic().getForum().getName() + " > " + lastPost.getTopic().getSubject();
				forumLastMessageTopicUrl = lastPost.getTopic().getFullPath();
			}
			
			ForumTopic mostActiveInTopic = forumController.findMostActiveTopicByAuthor(user);
			if (mostActiveInTopic != null) {
				Long mostActiveTopicPosts = forumController.countPostsByTopicAndAuthor(mostActiveInTopic, user);
				forumMostActiveInTopic = mostActiveInTopic.getForum().getName() + " > " + mostActiveInTopic.getSubject() + " (" + mostActiveTopicPosts + ")";
				forumMostActiveInTopicUrl = mostActiveInTopic.getFullPath();
			}
		}
		
		contactFieldHomePage = getContactField(user, UserContactFieldType.HOME_PAGE);
		contactFieldBlog = getContactField(user, UserContactFieldType.BLOG);
		contactFieldFacebook = getContactField(user, UserContactFieldType.FACEBOOK);
		contactFieldTwitter = getContactField(user, UserContactFieldType.TWITTER);
		contactFieldLinkedIn = getContactField(user, UserContactFieldType.LINKEDIN);
		contactFieldGooglePlus = getContactField(user, UserContactFieldType.GOOGLE_PLUS);
		hasContactInformation = StringUtils.isNotBlank(contactFieldHomePage)||
				StringUtils.isNotBlank(contactFieldBlog)||
				StringUtils.isNotBlank(contactFieldFacebook)||
				StringUtils.isNotBlank(contactFieldTwitter)||
				StringUtils.isNotBlank(contactFieldLinkedIn)||
				StringUtils.isNotBlank(contactFieldGooglePlus);
	}
	
	private String getContactField(User user, UserContactFieldType contactFieldType) {
	  return prepareContactField(userController.getContactFieldValue(user, contactFieldType));
	}
	
	private String prepareContactField(String value) {
	  if (StringUtils.isNotBlank(value)) {
	    if ((!StringUtils.startsWith(value, "http://")) && (!StringUtils.startsWith(value, "https://"))) {
	      return "http://" + value;
	    }
	  }
	  
    return value;
  }

  public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFullName() {
		return fullName;
	}
	
	public String getAbout() {
		return about;
	}
	
	public boolean publicationHasImages(Publication publication) {
		return publicationController.listPublicationImagesByPublication(publication).size() > 0;
	}

	public List<GameLibraryTag> getPublicationTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<>();

		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}

		return result;
	}
	
	public List<User> getPublicationAuthors(Publication publication) {
		List<User> result = new ArrayList<>(); 

		List<PublicationAuthor> publicationAuthors = publicationController.listPublicationAuthors(publication);
		for (PublicationAuthor publicationAuthor : publicationAuthors) {
			result.add(publicationAuthor.getAuthor());
		}
		
		return result;
	}
	
	public CreativeCommonsLicense getPublicationCreativeCommonsLicense(Publication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}

	
	public Long getPublicationCommentCount(Publication publication) {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
	}
	
	public String addPublicationToShoppingCart(Publication publication) {
		sessionShoppingCartController.addPublication(publication);
		return "pretty:gamelibrary-index";
	}
	
	public List<Publication> getPublishedPublications() {
		return publishedPublications;
	}
	
	public Boolean getHasGameLibraryPublications() {
		return hasGameLibraryPublications;
	}
	
	public Boolean getHasContactInformation() {
		return hasContactInformation;
	}
	
	public String getContactFieldHomePage() {
		return contactFieldHomePage;
	}
	
	public String getContactFieldBlog() {
		return contactFieldBlog;
	}
	
	public String getContactFieldFacebook() {
		return contactFieldFacebook;
	}

	public String getContactFieldTwitter() {
		return contactFieldTwitter;
	}
	
	public String getContactFieldLinkedIn() {
		return contactFieldLinkedIn;
	}

	public String getContactFieldGooglePlus() {
		return contactFieldGooglePlus;
	}
	
	public Long getForumTotalPosts() {
		return forumTotalPosts;
	}
	
	public String getForumLastMessageTopic() {
		return forumLastMessageTopic;
	}
	
	public String getForumLastMessageTopicUrl() {
		return forumLastMessageTopicUrl;
	}
	
	public String getForumMostActiveInTopic() {
		return forumMostActiveInTopic;
	}
	
	public String getForumMostActiveInTopicUrl() {
		return forumMostActiveInTopicUrl;
	}
	
	private Long userId;
	private String fullName;
	private String about;
	private Long forumTotalPosts;
	private String forumLastMessageTopic;
	private String forumLastMessageTopicUrl;
	private String forumMostActiveInTopic;
	private String forumMostActiveInTopicUrl;
	private Boolean hasGameLibraryPublications;
	private List<Publication> publishedPublications;
	private Boolean hasContactInformation;
	private String contactFieldHomePage;
	private String contactFieldBlog;
	private String contactFieldFacebook;
	private String contactFieldTwitter;
	private String contactFieldLinkedIn;
	private String contactFieldGooglePlus;
}

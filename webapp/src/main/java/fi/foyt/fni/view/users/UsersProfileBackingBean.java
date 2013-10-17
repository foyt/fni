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
import fi.foyt.fni.licences.CreativeCommonsLicense;
import fi.foyt.fni.licences.CreativeCommonsUtils;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.users.UserController;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "users-profile", 
		pattern = "/profile/#{usersProfileBackingBean.userId}", 
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
		
		contactFieldHomePage = userController.getContactFieldValue(user, UserContactFieldType.HOME_PAGE);
		contactFieldBlog = userController.getContactFieldValue(user, UserContactFieldType.BLOG);
		contactFieldFacebook = userController.getContactFieldValue(user, UserContactFieldType.FACEBOOK);
		contactFieldTwitter = userController.getContactFieldValue(user, UserContactFieldType.TWITTER);
		contactFieldLinkedIn = userController.getContactFieldValue(user, UserContactFieldType.LINKEDIN);
		contactFieldGooglePlus = userController.getContactFieldValue(user, UserContactFieldType.GOOGLE_PLUS);
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
	private List<Publication> publishedPublications;
	private String contactFieldHomePage;
	private String contactFieldBlog;
	private String contactFieldFacebook;
	private String contactFieldTwitter;
	private String contactFieldLinkedIn;
	private String contactFieldGooglePlus;
}

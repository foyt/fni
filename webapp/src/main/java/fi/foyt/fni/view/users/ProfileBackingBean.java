package fi.foyt.fni.view.users;

import java.io.FileNotFoundException;
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
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.store.Product;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.view.store.ProductController;

@RequestScoped
@Stateful
@Named
@URLMappings(mappings = {
  @URLMapping(
		id = "users-profile", 
		pattern = "/users/#{profileBackingBean.userId}", 
		viewId = "/users/profile.jsf"
  )
})
public class ProfileBackingBean {
	
	@Inject
	private UserController userController;
	
	@Inject
	private ForumController forumController;
	
	@Inject
	private ProductController productController;

	@URLAction
	public void init() throws FileNotFoundException {
		user = userController.findUserById(getUserId());
		if (user == null) {
			throw new FileNotFoundException();
		}
		
		latestPost = forumController.findLastPostByAuthor(user);
		mostActiveTopic = forumController.findMostActiveTopicByAuthor(user);
		mostActiveTopicPosts = mostActiveTopic != null ? forumController.countPostsByTopicAndAuthor(mostActiveTopic, user) : null;
		friends = userController.listUserFriends(user);
		publishedProducts = productController.listPublishedProductsByCreator(user);
		
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
	
	public User getUser() {
		return user;
	}
	
	public String getFullName() {
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
		
		return fullNameBuilder.toString();
	}
	
	public Long getForumPosts() {
		return forumController.countPostsByAuthor(user);
	}
	
	public ForumPost getLatestPost() {
		return latestPost;
	}
	
	public ForumTopic getMostActiveTopic() {
		return mostActiveTopic;
	}
	
	public Long getMostActiveTopicPosts() {
		return mostActiveTopicPosts;
	}
	
	public List<User> getFriends() {
		return friends;
	}
	
	public List<Product> getPublishedProducts() {
		return publishedProducts;
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
	
	private Long userId;
	private User user;
	private ForumPost latestPost;
	private ForumTopic mostActiveTopic;
	private Long mostActiveTopicPosts;	
	private List<User> friends;
	private List<Product> publishedProducts;
	private String contactFieldHomePage;
	private String contactFieldBlog;
	private String contactFieldFacebook;
	private String contactFieldTwitter;
	private String contactFieldLinkedIn;
	private String contactFieldGooglePlus;
}

package fi.foyt.fni.view.users;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Matches;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;

import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.jsf.NavigationController;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationAuthor;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;
import fi.foyt.fni.persistence.model.illusion.IllusionEventParticipantRole;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.persistence.model.users.UserContactFieldType;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.licenses.CreativeCommonsLicense;
import fi.foyt.fni.utils.licenses.CreativeCommonsUtils;

@RequestScoped
@Stateful
@Named
@Join (path = "/profile/{id}", to = "/users/profile.jsf" )
public class UsersProfileBackingBean {
  
  @Parameter
  @Matches ("[0-9]{1,}")
  private Long id;
  
	@Inject
	private UserController userController;

	@Inject
	private PublicationController publicationController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;

	@Inject
	private ForumController forumController;

  @Inject
	private IllusionEventController illusionEventController;

  @Inject
  private NavigationController navigationController;

	@RequestAction 
	public String init() {
    this.userId = id;

    User user = userController.findUserById(getId());
		if (user == null) {
		  return navigationController.notFound();
		}
		
		if (user.getArchived()) {
      return navigationController.notFound();
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
		
		List<IllusionEvent> events = illusionEventController.listPublishedEventsByUserAndRole(user, IllusionEventParticipantRole.ORGANIZER);
		Collections.sort(events, new Comparator<IllusionEvent>() {
		  @Override
		  public int compare(IllusionEvent o1, IllusionEvent o2) {
		    return o2.getStart().compareTo(o1.getStart());
		  }
    });
		
		this.organizerInEvents = createEventPojos(events);
		
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
		contactFieldInstagram = getContactField(user, UserContactFieldType.INSTAGRAM);
		
		hasContactInformation = hasContactInformation();
		
		return null;
	}
	
	private boolean hasContactInformation() {
	  return hasContactField(contactFieldHomePage, contactFieldBlog, contactFieldFacebook, 
	      contactFieldTwitter, contactFieldLinkedIn, contactFieldGooglePlus, contactFieldInstagram); 
	}
	
	private boolean hasContactField(String... fields) {
	  for (String field : fields) {
	    if (StringUtils.isNotBlank(field)) {
	      return true;
	    }
	  }
	  
	  return false;
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

  public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
	  return userId;
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
	
	public CreativeCommonsLicense getPublicationCreativeCommonsLicense(BookPublication publication) {
		return CreativeCommonsUtils.parseLicenseUrl(publication.getLicense());
	}

	
	public Long getPublicationCommentCount(Publication publication) {
		if (publication.getForumTopic() != null) {
			return forumController.countPostsByTopic(publication.getForumTopic());
		}
		
		return null;
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
	
	public String getContactFieldInstagram() {
    return contactFieldInstagram;
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
	
	public List<Event> getOrganizerInEvents() {
    return organizerInEvents;
  }
	
  public String replaceDescriptionLineBreaks(String description) {
    if (StringUtils.isNotBlank(description)) {
      return description.replace("\n", "<br/>");  
    }
    
    return null;
  }

  private List<Event> createEventPojos(List<IllusionEvent> illusionEvents) {
    List<Event> result = new ArrayList<>();
    
    for (IllusionEvent illusionEvent : illusionEvents) {
      result.add(createEventPojo(illusionEvent));
    }
    
    return result;
  }
  
  private Event createEventPojo(IllusionEvent event) {
    if (DateUtils.isSameDay(event.getStart(), event.getEnd())) {
      Date date = DateUtils.truncate(event.getStart(), Calendar.DAY_OF_MONTH);
      long startDiff = event.getStart().getTime() - date.getTime();
      long endDiff = event.getEnd().getTime() - date.getTime();
      Date startTime = startDiff > 0 ? new Date(startDiff) : null;
      Date endTime = endDiff > 0 ? new Date(endDiff) : null;
      return new Event(event.getName(), event.getUrlName(), event.getDescription(), event.getStart(), startTime, event.getEnd(), endTime);
    } else {
      return new Event(event.getName(), event.getUrlName(), event.getDescription(), event.getStart(), null, event.getEnd(), null);
    }
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
  private String contactFieldInstagram;
	private List<Event> organizerInEvents;
	
  public class Event {
    
    public Event(String name, String urlName, String description, Date startDate, Date startTime, Date endDate, Date endTime) {
      super();
      this.name = name;
      this.urlName = urlName;
      this.description = description;
      this.startDate = startDate;
      this.startTime = startTime;
      this.endDate = endDate;
      this.endTime = endTime;
    }

    public String getName() {
      return name;
    }
    
    public String getUrlName() {
      return urlName;
    }
    
    public String getDescription() {
      return description;
    }
    
    public Date getStartDate() {
      return startDate;
    }
    
    public Date getStartTime() {
      return startTime;
    }
    
    public Date getEndDate() {
      return endDate;
    }
    
    public Date getEndTime() {
      return endTime;
    }

    private String name;
    private String urlName;
    private String description;
    private Date startDate;
    private Date startTime;
    private Date endDate;
    private Date endTime;
  }
}

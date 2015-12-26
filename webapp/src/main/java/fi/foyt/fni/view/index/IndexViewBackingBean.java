package fi.foyt.fni.view.index;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.ocpsoft.rewrite.annotation.Join;

import fi.foyt.fni.blog.BlogController;
import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.GameLibraryTagController;
import fi.foyt.fni.gamelibrary.PublicationController;
import fi.foyt.fni.illusion.IllusionEventController;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogTag;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.BookPublication;
import fi.foyt.fni.persistence.model.gamelibrary.GameLibraryTag;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.persistence.model.gamelibrary.PublicationTag;
import fi.foyt.fni.persistence.model.illusion.IllusionEvent;

@RequestScoped
@Named
@Stateful
@Join (path = "/", to = "/index.jsf")
public class IndexViewBackingBean {
	
	private static final int MAX_GAME_LIBRARY_PUBLICATIONS = 2;
	
  private static final int MAX_ILLUSION_EVENTS = 2;
	
	private static final int MAX_LATEST_ENTRIES = 5;
	
	private static final int MAX_FORUM_TOPICS = 19;
	
	private static final int DEFAULT_FEED_ENTRIES = 3;

	@Inject
	private BlogController blogController;

	@Inject
	private PublicationController publicationController;

	@Inject
	private GameLibraryTagController gameLibraryTagController;
	
	@Inject
	private ForumController forumController;

  @Inject
	private IllusionEventController illusionEventController;
	
	@PostConstruct
	public void init() {
		latestGameLibraryPublications = publicationController.listRecentBookPublications(MAX_GAME_LIBRARY_PUBLICATIONS);
		latestForumTopics = forumController.listLatestForumTopics(MAX_FORUM_TOPICS);
		illusionEvents = createEventPojos(illusionEventController.listNextIllusionEvents(MAX_ILLUSION_EVENTS));
	
    DateTime lastPostDate = blogController.getLastBlogDate();
    if (lastPostDate != null) {
      newsArchiveMonth = lastPostDate.getMonthOfYear();
      newsArchiveYear = lastPostDate.getYear();
    }
	}
	
	public List<BlogEntry> getLatestBlogEntries() {
		return blogController.listBlogEntries(MAX_LATEST_ENTRIES);
	}

	public List<BlogEntry> getBlogFeed() {
		return blogController.listBlogEntries(DEFAULT_FEED_ENTRIES);
	}
	
	public List<BlogTag> getBlogEntryTags(BlogEntry entry) {
		return blogController.listBlogEntryTags(entry);
	}
	
	public List<BookPublication> getLatestGameLibraryPublications() {
		return latestGameLibraryPublications;
	}
	
	public List<GameLibraryTag> getPublicationTags(Publication publication) {
		List<GameLibraryTag> result = new ArrayList<>();
		
		List<PublicationTag> publicationTags = gameLibraryTagController.listPublicationTags(publication);
		for (PublicationTag publicationTag : publicationTags) {
			result.add(publicationTag.getTag());
		}
		
		return result;
	}
	
	public List<ForumTopic> getLatestForumTopics() {
		return latestForumTopics;
	}
	
	public List<Event> getIllusionEvents() {
    return illusionEvents;
  }
	
	public int getNewsArchiveMonth() {
    return newsArchiveMonth;
  }
	
	public int getNewsArchiveYear() {
    return newsArchiveYear;
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
	
	private List<BookPublication> latestGameLibraryPublications;
	private List<ForumTopic> latestForumTopics;
	private List<Event> illusionEvents;
	private int newsArchiveYear;
	private int newsArchiveMonth;
	
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

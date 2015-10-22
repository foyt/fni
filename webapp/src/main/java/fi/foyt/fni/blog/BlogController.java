package fi.foyt.fni.blog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import fi.foyt.fni.persistence.dao.blog.BlogCategoryDAO;
import fi.foyt.fni.persistence.dao.blog.BlogEntryDAO;
import fi.foyt.fni.persistence.dao.blog.BlogEntryTagDAO;
import fi.foyt.fni.persistence.dao.blog.BlogTagDAO;
import fi.foyt.fni.persistence.model.blog.BlogCategory;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogEntryTag;
import fi.foyt.fni.persistence.model.blog.BlogTag;

public class BlogController {
	
	@Inject
	private Logger logger;

	@Inject
	private BlogEntryDAO blogEntryDAO;

	@Inject
	private BlogEntryTagDAO blogEntryTagDAO;

	@Inject
	private BlogTagDAO blogTagDAO;

	@Inject
	private BlogCategoryDAO blogCategoryDAO;
	
	/* BlogEntries */
	
	public List<BlogEntry> listBlogEntries(int maxEntries) {
		return blogEntryDAO.listAllSortByCreated(0, maxEntries);
	}

  public List<BlogEntry> listBlogEntriesByYearAndMonth(Integer year, Integer month) {
    GregorianCalendar calendar = new GregorianCalendar();
    calendar.set(year, month, 0, 0, 0, 0);
    Date after = calendar.getTime();
    calendar.add(Calendar.MONTH, 1);
    Date before = calendar.getTime();
    return listBlogEntriesByCreatedBetween(after, before);
  }

  public List<BlogEntry> listBlogEntriesByCreatedBetween(Date after, Date before) {
    return blogEntryDAO.listByCreatedGreaterOrEqualAndCreatedLessOrEqualSortByCreated(after, before);
  }
  
  public DateTime getFirstBlogDate() {
    Date date = blogEntryDAO.minBlogDate();
    return date != null ? new DateTime(date.getTime()) : null;
  }
  
  public DateTime getLastBlogDate() {
    Date date = blogEntryDAO.maxBlogDate();
    return date != null ? new DateTime(date.getTime()) : null;
  }

  public Long countBlogEntriesByCreatedBetween(Date after, Date before) {
    return blogEntryDAO.countByCreatedGreaterOrEqualAndCreatedLessOrEqualSortByCreated(after, before);
  }
  
	/* BlogTags */
	
	public List<BlogTag> listBlogEntryTags(BlogEntry entry) {
		return blogEntryTagDAO.listTagsByBlogEntry(entry);
	}
	
	/* BlogCategories */
	
	public List<BlogCategory> listUnsynchronizedBlogCategories() {
		return blogCategoryDAO.listBySyncNeNullAndNextSyncLe(new Date());
	}

	@SuppressWarnings("unchecked")
	public BlogCategory synchronizeEntries(BlogCategory blogCategory) {
		SyndFeedInput input = new SyndFeedInput();
		try {
			SyndFeed feed = input.build(new XmlReader(new URL(blogCategory.getSyncUrl())));
			Iterator<SyndEntry> entryIterator = feed.getEntries().iterator();
			while (entryIterator.hasNext()) {
				SyndEntry syndEntry = entryIterator.next();
				String title = syndEntry.getTitle();
				String summary = syndEntry.getDescription().getValue();
				Date created = syndEntry.getPublishedDate();
				Date modified = syndEntry.getUpdatedDate();
				if (modified == null) {
					modified = created;
				}
				String link = syndEntry.getLink();
				String guid = syndEntry.getUri();
				String authorName = syndEntry.getAuthor();
				List<String> categories = new ArrayList<>();
				for (SyndCategory category : (List<SyndCategory>) syndEntry.getCategories()) {
					categories.add(StringEscapeUtils.unescapeXml(category.getName()));
				};
				
  			synchronizeEntry(blogCategory, title, summary, modified, created, link, guid, authorName, categories);
				
			}
			
			scheduleNextSync(blogCategory);
			
		} catch (IllegalArgumentException | FeedException | IOException e) {
			logger.log(Level.WARNING, "RSS Synchronization failed", e);
		}
		
		return blogCategory;
	}
	
	private void synchronizeEntry(BlogCategory blogCategory, String title, String summary, Date modified, Date created, String link, String guid, String authorName, List<String> categories) {
		if (created == null) {
			created = new Date();
		}
		
		if (modified == null) {
			modified = created;
		}
		
		if (StringUtils.endsWith(summary, "[...]")) {
			summary = summary.substring(0, summary.length() - 5);
		} else if (StringUtils.endsWith(summary, "[&#8230;]")) {
			summary = summary.substring(0, summary.length() - 9);
		}
		
		summary = StringUtils.strip(summary);

		if (StringUtils.isNotBlank(guid)) {
			BlogEntry blogEntry = blogEntryDAO.findByGuid(guid);
			if (blogEntry != null) {
				blogEntryDAO.updateAuthorName(blogEntry, authorName);
				blogEntryDAO.updateTitle(blogEntry, title);
				blogEntryDAO.updateLink(blogEntry, link);
				blogEntryDAO.updateSummary(blogEntry, summary);
				blogEntryDAO.updateModified(blogEntry, modified);
			} else {
				blogEntry = blogEntryDAO.create(guid, blogCategory, null, authorName, link, title, summary, null, created, null, modified, null);
			}
			
			List<BlogTag> entryTags = new ArrayList<>();

			for (String category : categories) {
				BlogTag blogTag = blogTagDAO.findByText(category);

				if (blogTag == null) {
					blogTag = blogTagDAO.create(category);
				}
				
				entryTags.add(blogTag);
			}
			
			List<BlogEntryTag> deleteEntryTags = blogEntryTagDAO.listByEntryAndTagNotIn(blogEntry, entryTags);
			for (BlogEntryTag deleteEntryTag : deleteEntryTags) {
				blogEntryTagDAO.delete(deleteEntryTag);
			}
			
			for (BlogTag entryTag : entryTags) {
				if (blogEntryTagDAO.findByEntryAndTag(blogEntry, entryTag) == null) {
					blogEntryTagDAO.create(blogEntry, entryTag);
				}
			}
		} else {
			logger.warning("RSS Feed Entry did not contain a valid guid, skipping");
		}
	}

	private void scheduleNextSync(BlogCategory blogCategory) {
		Date now = new Date();
	  
	  switch (blogCategory.getSync()) {
	  	case HOURLY:
	  		blogCategoryDAO.updateNextSync(blogCategory, DateUtils.addHours(now, 1));
	  	break;
	  	case DAILY:
	  		blogCategoryDAO.updateNextSync(blogCategory, DateUtils.addDays(now, 1));
	  	break;
	  	case MONTHLY:
	  		blogCategoryDAO.updateNextSync(blogCategory, DateUtils.addMonths(now, 1));
	  	break;
	  }
	}

}

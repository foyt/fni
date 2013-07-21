package fi.foyt.fni.blog;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.rss.RssItem;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import fi.foyt.fni.persistence.dao.blog.BlogCategoryDAO;
import fi.foyt.fni.persistence.dao.blog.BlogEntryDAO;
import fi.foyt.fni.persistence.dao.blog.BlogEntryTagDAO;
import fi.foyt.fni.persistence.dao.blog.BlogTagDAO;
import fi.foyt.fni.persistence.model.blog.BlogCategory;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogEntryTag;
import fi.foyt.fni.persistence.model.blog.BlogTag;

@Stateful
@Dependent
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
	
	/* BlogTags */
	
	public List<BlogTag> listBlogEntryTags(BlogEntry entry) {
		return blogEntryTagDAO.listTagsByBlogEntry(entry);
	}
	
	/* BlogCategories */
	
	public List<BlogCategory> listUnsynchronizedBlogCategories() {
		return blogCategoryDAO.listBySyncNeNullAndNextSyncLe(new Date());
	}
	
	public BlogCategory synchronizeEntries(BlogCategory blogCategory) {
	  Abdera abdera = new Abdera();
	  AbderaClient client = new AbderaClient(abdera);
	  ClientResponse resp = client.get(blogCategory.getSyncUrl());
	  if (resp.getType() == ResponseType.SUCCESS) {
	    Document<Feed> doc = resp.getDocument();
	    Feed root = doc.getRoot();
	    
	    List<Entry> entries = root.getEntries();
	    for (Entry entry : entries) {
	    	if (entry instanceof RssItem) {
	    		RssItem rssItem = (RssItem) entry;
	    		String link = null;
	    		String guid = null;
	    		String authorName = null;
	    		Person author = rssItem.getAuthor();
	    		if (author != null) {
	    			authorName = author.getText();
	    		}
	    		
	    		Link alternateLink = rssItem.getAlternateLink();
	    		if (alternateLink != null) {
						try {
							link = alternateLink.getHref().toURL().toExternalForm();
						} catch (MalformedURLException | URISyntaxException e) {
	    				logger.log(Level.WARNING, "RSS Feed Entry contained erroneous link", e);
						}
						
						try {
							guid = rssItem.getId().toURL().toExternalForm();
						} catch (MalformedURLException | URISyntaxException e) {
	    				logger.log(Level.WARNING, "RSS Feed Entry contained erroneous GUID", e);
						}
	    			
	    			Date modified = rssItem.getEdited();
	    			Date created = rssItem.getPublished();
	    			
	    			if (created == null) {
	    				created = new Date();
	    			}
	    			
	    			if (modified == null) {
	    				modified = created;
	    			}
	    			
	    			String summary = entry.getSummary();
	    			if (StringUtils.endsWith(summary, "[...]")) {
	    				summary = summary.substring(0, summary.length() - 5);
	    			}
	    			summary = StringUtils.strip(summary);

	    			if (StringUtils.isNotBlank(guid)) {
	    				BlogEntry blogEntry = blogEntryDAO.findByGuid(guid);
	    				if (blogEntry != null) {
	    					blogEntryDAO.updateAuthorName(blogEntry, authorName);
	    					blogEntryDAO.updateTitle(blogEntry, entry.getTitle());
	    					blogEntryDAO.updateLink(blogEntry, link);
	    					blogEntryDAO.updateSummary(blogEntry, summary);
	    					blogEntryDAO.updateModified(blogEntry, modified);
	    				} else {
	    					blogEntry = blogEntryDAO.create(guid, blogCategory, null, authorName, link, entry.getTitle(), summary, null, created, null, modified, null);
	    				}
	    				
	    				List<BlogTag> entryTags = new ArrayList<>();

	    				for (Category category : entry.getCategories()) {
		    				String text = category.getText();
		    				BlogTag blogTag = blogTagDAO.findByText(text);
		    				if (blogTag == null) {
		    					blogTag = blogTagDAO.create(text);
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
	    	} else {
  				logger.warning("Unknown Feed Entry type, skipping");
	    	}
	    }
	  } else {
			logger.warning("RSS Synchronization failed for " + blogCategory.getSyncUrl() + " with following error: " + resp.getStatusText());
	  }
	  
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
	  
	  return blogCategory;
	}

}

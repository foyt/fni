package fi.foyt.fni.view.index;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.blog.BlogController;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogTag;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
public class IndexViewBackingBean {

	private static final int MAX_LATEST_ENTRIES = 10;
	
	private static final int DEFAULT_FEED_ENTRIES = 3;
	
	@Inject
	private Logger logger;

	@Inject
	private SessionController sessionController;

	@Inject
	private BlogController blogController;
	
	public List<BlogEntry> getLatestBlogEntries() {
		return blogController.listBlogEntries(MAX_LATEST_ENTRIES);
	}

	public List<BlogEntry> getBlogFeed() {
		return blogController.listBlogEntries(DEFAULT_FEED_ENTRIES);
	}
	
	public List<BlogTag> getBlogEntryTags(BlogEntry entry) {
		return blogController.listBlogEntryTags(entry);
	}
	
	public void setBlogEntryFilterTag(String blogEntryFilterTag) {
		this.blogEntryFilterTag = blogEntryFilterTag;
	}
	
	public String getBlogEntryFilterTag() {
		return blogEntryFilterTag;
	}
	
	private String blogEntryFilterTag;
}

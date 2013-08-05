package fi.foyt.fni.view.index;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import fi.foyt.fni.blog.BlogController;
import fi.foyt.fni.forum.ForumController;
import fi.foyt.fni.gamelibrary.ProductController;
import fi.foyt.fni.persistence.model.blog.BlogEntry;
import fi.foyt.fni.persistence.model.blog.BlogTag;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.gamelibrary.Publication;
import fi.foyt.fni.session.SessionController;

@RequestScoped
@Named
@Stateful
public class IndexViewBackingBean {
	
	private static final int MAX_GAME_LIBRARY_PUBLICATIONS = 5;
	
	private static final int MAX_LATEST_ENTRIES = 5;
	
	private static final int MAX_FORUM_TOPICS = 5;
	
	private static final int DEFAULT_FEED_ENTRIES = 3;
	
	@Inject
	private Logger logger;

	@Inject
	private SessionController sessionController;

	@Inject
	private BlogController blogController;

	@Inject
	private ProductController productController;

	@Inject
	private ForumController forumController;
	
	@PostConstruct
	public void init() {
		latestGameLibraryPublications = productController.listRecentProducts(MAX_GAME_LIBRARY_PUBLICATIONS);
		latestForumTopics = forumController.listLatestForumTopics(MAX_FORUM_TOPICS);
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
	
	public List<Publication> getLatestGameLibraryPublications() {
		return latestGameLibraryPublications;
	}
	
	public List<ForumTopic> getLatestForumTopics() {
		return latestForumTopics;
	}
	
	private List<Publication> latestGameLibraryPublications;
	private List<ForumTopic> latestForumTopics;
}

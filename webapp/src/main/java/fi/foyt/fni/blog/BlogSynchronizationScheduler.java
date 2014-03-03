package fi.foyt.fni.blog;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.blog.BlogCategory;

@Singleton
public class BlogSynchronizationScheduler {

  @Inject
  private Logger logger;

  @Inject
  private BlogController blogController;

  @Schedule(dayOfWeek = "*", hour = "*", minute = "0", second = "0", year = "*", persistent = false)
  public synchronized void synchronizeFeeds() {
  	logger.info("Scheduled blog rss synchronization started");
  	
  	List<BlogCategory> blogCategories = blogController.listUnsynchronizedBlogCategories();
  	for (BlogCategory blogCategory : blogCategories) {
  		blogController.synchronizeEntries(blogCategory);
  	}

  	logger.info("Scheduled blog rss synchronization finished");
  }

}

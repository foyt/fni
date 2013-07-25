package fi.foyt.fni.forum;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import fi.foyt.fni.persistence.dao.forum.ForumCategoryDAO;
import fi.foyt.fni.persistence.dao.forum.ForumDAO;
import fi.foyt.fni.persistence.dao.forum.ForumPostDAO;
import fi.foyt.fni.persistence.dao.forum.ForumTopicDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumCategory;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.utils.servlet.RequestUtils;

@Dependent
@Stateful
public class ForumController {

	@Inject
	private ForumCategoryDAO forumCategoryDAO;

	@Inject
	private ForumDAO forumDAO;
	
	@Inject
	private ForumTopicDAO forumTopicDAO;

	@Inject
	private ForumPostDAO forumPostDAO;
	
	// Categories
	
	public List<ForumCategory> listForumCategories() {
		return forumCategoryDAO.listAll();
	}
	
	// Forums
	
	public Forum findForumById(Long id) {
		return forumDAO.findById(id);
	}

	public Forum findForumByUrlName(String urlName) {
		return forumDAO.findByUrlName(urlName);
	}
	
	public List<Forum> listForumsByCategory(ForumCategory category) {
		return forumDAO.listByCategory(category);
	}
	
	// Topics

	public ForumTopic createTopic(Forum forum, String subject, User author) {
		Date now = new Date();
		return forumTopicDAO.create(forum, author, now, now, createUrlName(forum, subject), subject, 0l);
	}

	public ForumTopic findForumTopicByForumAndUrlName(Forum forum, String urlName) {
		return forumTopicDAO.findByForumAndUrlName(forum, urlName);
	}
	
	public ForumTopic findForumTopicByUrlNames(String forumUrlName, String topicUrlName) {
		return findForumTopicByForumAndUrlName(findForumByUrlName(forumUrlName), topicUrlName);
	}

	public List<ForumTopic> listTopicsByForum(Forum forum) {
		return forumTopicDAO.listByForum(forum);
	}	

	public Long countPostsByTopic(ForumTopic topic) {
		return forumPostDAO.countByTopic(topic);
	}

	public Long countTopicsByForum(Forum forum) {
		return forumTopicDAO.countByForum(forum);
	}

	public ForumTopic updateTopicViews(ForumTopic topic, long views) {
    return forumTopicDAO.updateViews(topic, views);
	}	
	
	// Posts

	public ForumPost createForumPost(ForumTopic topic, User author, String content) {
		Date now = new Date();
		return forumPostDAO.create(topic, author, now, now, content, 0l);
	}
	
	public ForumPost findLastTopicPost(ForumTopic forumTopic) {
		ForumPost lastTopicPost = null;
    List<ForumPost> forumPosts = forumPostDAO.listByTopicSortByCreated(forumTopic, 0, 1);
    if (forumPosts.size() == 1)
      lastTopicPost = forumPosts.get(0);
    
    return lastTopicPost;
  }
	
	public List<ForumPost> listPostsByTopic(ForumTopic topic) {
		return forumPostDAO.listByTopic(topic);
	}

	public Long countPostsByAuthor(User author) {
		return forumPostDAO.countByAuthor(author);
  }

	public ForumPost updatePostViews(ForumPost post, long views) {
    return forumPostDAO.updateViews(post, views);
	}

	public Long countPostsByForum(Forum forum) {
		return forumPostDAO.countByForum(forum);
	}

	public ForumPost getLastPostByTopic(ForumTopic topic) {
		List<ForumPost> posts = forumPostDAO.listByTopicSortByCreated(topic, 0, 1);
		if (posts.size() == 1) {
			return posts.get(0);
		}
		
		return null;
	}

	public ForumPost getLastPostByForum(Forum forum) {
		List<ForumPost> posts = forumPostDAO.listByForumSortByCreated(forum, 0, 1);
		if (posts.size() == 1) {
			return posts.get(0);
		}
		
		return null;
	}

	private String createUrlName(Forum forum, String subject) {
		int maxLength = 20;
		int padding = 0;
		do {
			String urlName = RequestUtils.createUrlName(subject, maxLength);
			if (padding > 0) {
				urlName = urlName.concat(StringUtils.repeat('_', padding));
			}
			
			ForumTopic topic = forumTopicDAO.findByForumAndUrlName(forum, urlName);
			if (topic == null) {
				return urlName;
			}
			
			if (maxLength < subject.length()) {
				maxLength++;
			} else {
				padding++;
			}
		} while (true);
	}

}

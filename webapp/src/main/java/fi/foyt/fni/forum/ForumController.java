package fi.foyt.fni.forum;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import fi.foyt.fni.persistence.dao.forum.ForumDAO;
import fi.foyt.fni.persistence.dao.forum.ForumPostDAO;
import fi.foyt.fni.persistence.dao.forum.ForumTopicDAO;
import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.users.User;

@RequestScoped
@Stateful
public class ForumController {

	@Inject
	private ForumDAO forumDAO;
	
	@Inject
	private ForumTopicDAO forumTopicDAO;

	@Inject
	private ForumPostDAO forumPostDAO;
	
	// Forums
	
	public Forum findForumByUrlName(String urlName) {
		return forumDAO.findByUrlName(urlName);
	}
	
	// Topics
	
	public ForumTopic findForumTopicByForumAndUrlName(Forum forum, String urlName) {
		return forumTopicDAO.findByForumAndUrlName(forum, urlName);
	}
	
	public ForumTopic findForumTopicByUrlNames(String forumUrlName, String topicUrlName) {
		return findForumTopicByForumAndUrlName(findForumByUrlName(forumUrlName), topicUrlName);
	}

	public List<ForumTopic> listTopicsByForum(Forum forum) {
		return forumTopicDAO.listByForum(forum);
	}	

	public Long countTopicReplies(ForumTopic topic) {
		return forumPostDAO.countByTopic(topic) - 1;
	}

	public ForumTopic updateTopicViews(ForumTopic topic, long views) {
    return forumTopicDAO.updateViews(topic, views);
	}	
	
	// Posts

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
}

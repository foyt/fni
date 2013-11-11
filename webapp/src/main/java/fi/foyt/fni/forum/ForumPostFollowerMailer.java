package fi.foyt.fni.forum;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import fi.foyt.fni.persistence.model.forum.Forum;
import fi.foyt.fni.persistence.model.forum.ForumFollower;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.forum.ForumTopicFollower;

@Stateless
public class ForumPostFollowerMailer {

  @Inject
  private ForumController forumController;
  
  public void onForumPostCreated(@Observes @ForumPostCreated ForumPostEvent event) {
    ForumPost forumPost = forumController.findForumPostById(event.getForumPostId());
    if (forumPost != null) {
      List<Long> notifyUsers = new ArrayList<>(); 
      
      ForumTopic forumTopic = forumPost.getTopic();
      Forum forum = forumTopic.getForum();
      
      List<ForumFollower> forumFollowers = forumController.listForumFollowers(forum);
      List<ForumTopicFollower> topicFollowers = forumController.listForumTopicFollowers(forumTopic);
      
      for (ForumFollower forumFollower : forumFollowers) {
        if (!notifyUsers.contains(forumFollower.getUser().getId())) {
          notifyUsers.add(forumFollower.getUser().getId());
        }
      }
      
      for (ForumTopicFollower topicFollower : topicFollowers) {
        if (!notifyUsers.contains(topicFollower.getUser().getId())) {
          notifyUsers.add(topicFollower.getUser().getId());
        }
      }

      // TODO: Send posts
    }
  }

}

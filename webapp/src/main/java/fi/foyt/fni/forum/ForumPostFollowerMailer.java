package fi.foyt.fni.forum;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.commons.lang3.LocaleUtils;

import fi.foyt.fni.i18n.ExternalLocales;
import fi.foyt.fni.persistence.model.forum.ForumTopicWatcher;
import fi.foyt.fni.persistence.model.forum.ForumPost;
import fi.foyt.fni.persistence.model.forum.ForumTopic;
import fi.foyt.fni.persistence.model.system.SystemSettingKey;
import fi.foyt.fni.persistence.model.users.User;
import fi.foyt.fni.session.SessionController;
import fi.foyt.fni.system.SystemSettingsController;
import fi.foyt.fni.users.UserController;
import fi.foyt.fni.utils.mail.MailUtils;
import fi.foyt.fni.view.forum.ForumTopicBackingBean;

@Stateless
public class ForumPostFollowerMailer {

  @Inject
  private Logger logger;

  @Inject
  private UserController userController;

  @Inject
  private SessionController sessionController;

  @Inject
  private ForumController forumController;

  @Inject
  private SystemSettingsController systemSettingsController;
  
  public void onForumPostCreated(@Observes @ForumPostCreated ForumPostEvent event) {
    ForumPost forumPost = forumController.findForumPostById(event.getForumPostId());
    if (forumPost != null) {
      List<Long> notifyUserIds = new ArrayList<>(); 
      
      ForumTopic forumTopic = forumPost.getTopic();
      List<ForumTopicWatcher> topicWatchers = forumController.listForumTopicWatchers(forumTopic);
      
      for (ForumTopicWatcher topicWatcher : topicWatchers) {
        if (!notifyUserIds.contains(topicWatcher.getUser().getId())) {
          notifyUserIds.add(topicWatcher.getUser().getId());
        }
      }
      
      notifyUserIds.remove(sessionController.getLoggedUserId());

      for (Long notifyUserId : notifyUserIds) {
        User user = userController.findUserById(notifyUserId);
        try {
          notifyUser(event.getLocalAddress(), event.getContextPath(), forumPost, user);
        } catch (MessagingException e) {
          logger.log(Level.WARNING, "Could not send notification email", e);
        }
      }
    }
  }

  private void notifyUser(String localAddress, String contextPath, ForumPost forumPost, User user) throws MessagingException {
    Locale locale = Locale.getDefault();
    
    try {
      locale = LocaleUtils.toLocale(user.getLocale());
    } catch (IllegalArgumentException e) {
      // Invalid locale has somehow ended up in the database
      logger.log(Level.SEVERE, "Invalid locale found from User", e);
    }
    
    ForumTopic topic = forumPost.getTopic();
    long pageCount = Math.round(Math.ceil(new Double(forumController.countPostsByTopic(topic)) / ForumTopicBackingBean.POST_PER_PAGE));
    
    String link = new StringBuilder()
      .append(localAddress)
      .append(contextPath)
      .append("/forum/")
      .append(topic.getForum().getUrlName())
      .append('/')
      .append(topic.getUrlName())
      .append("?page=")
      .append(pageCount - 1)
      .append("#p")
      .append(forumPost.getId())
      .toString();
    
    String userEmail = userController.getUserPrimaryEmail(user);
    String subject = ExternalLocales.getText(locale, "forum.mail.newPost.subject");
    String content = ExternalLocales.getText(locale, "forum.mail.newPost.content", link);
    String fromName = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_NAME);
    String fromMail = systemSettingsController.getSetting(SystemSettingKey.SYSTEM_MAILER_MAIL);
    
    MailUtils.sendMail(fromMail, fromName, userEmail, user.getFullName(), subject, content, "text/plain");
  }

}

package fi.foyt.fni.test.ui.base.forum;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;

import fi.foyt.fni.persistence.model.users.UserSettingKey;
import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.AbstractUITest;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql"  }),
  @DefineSqlSet (id = "forum-basic", before = { "basic-forum-setup.sql" }, after = { "basic-forum-teardown.sql" }),
  @DefineSqlSet (id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet (id = "event", before = { "illusion-event-open-setup.sql" }, after = { "illusion-event-open-teardown.sql" }),
  @DefineSqlSet (id = "event-forum", before = { "illusion-event-open-forum-setup.sql" }, after = {"illusion-event-open-forum-teardown.sql"}),
  @DefineSqlSet (id = "forum-with-special-characters", before = { "forum-with-special-characters-setup.sql"}, after={"forum-with-special-characters-teardown.sql" }),
  @DefineSqlSet (id = "forum-watchers", before = "forum-watchers-setup.sql", after = "forum-watchers-teardown.sql")
})
public class ForumTopicTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTexts() {
    navigate("/forum/5_topic_forum/topic5of5");
    assertSelectorText(".view-header-description-title", "FIVE TOPIC FORUM", true, true);
    assertSelectorText("h4", "TOPIC 5 OF 5 TOPIC FORUM", true, true);
    assertSelectorText(".forum-topic-created-info span", "STARTED AT JAN 1, 2010 BY", true, true);
    assertSelectorText(".forum-topic-created-info a", "TEST GUEST", true, true);
    assertSelectorLink(".forum-topic-created-info a", String.format("%s/profile/1", getAppUrl()));
  }

  private void assertSelectorLink(String selector, String link) {
    assertEquals(link, findElementBySelector(selector).getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testAnonymous() {
    navigate("/forum/5_topic_forum/topic5of5");
    assertSelectorNotVisible(".forum-topic-post-editor-container");
    assertSelectorVisible(".forum-topic-reply-login-link");
    assertSelectorTextIgnoreCase(".forum-topic-reply-login-link", "LOGIN TO POST A REPLY");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigate("/forum/5_topic_forum/topic5of5");
    assertSelectorVisible(".forum-topic-post-editor-container");
    scrollIntoView(".forum-topic-post-editor-container");
    waitForSelectorVisible(".forum-topic-post-editor-container .cke_wysiwyg_frame");
    assertSelectorVisible(".forum-topic-post-editor-container .cke_wysiwyg_frame");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testNotFound() throws Exception {
    testNotFound("/forum/5_topic_forum/invalid");
    testNotFound("/forum/5_topic_forum/1234");
    testNotFound("/forum/5_topic_forum/~");
    testNotFound("/forum///asd");
    testNotFound("/forum/*/*");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "forum-with-special-characters"})
  public void testWithHyphen() throws Exception {
    navigate("/forum/with-special.characters/with-special.characters");
    assertTitle("Forum");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "illusion-basic", "event", "event-forum"})
  public void testInvisible() throws Exception {
    testNotFound("/forum/illusion/openevent");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "forum-watchers" })
  public void testPostDefaultNotification() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      loginInternal("user@foyt.fi", "pass");
      navigate("/forum/1_topic_forum/single_topic");
      assertSelectorVisible(".forum-topic-post-editor-container");
      scrollIntoView(".forum-topic-post-editor-container");
      waitForSelectorVisible(".forum-topic-post-editor-container .cke_wysiwyg_frame");
      setCKEditorContents(0, "<p>Test post</p>");
      scrollWaitAndClick(".forum-topic-post-send-container input[type='submit']"); 
      waitForSelectorCount(".post", 2);
      
      assertEquals(1, greenMail.getReceivedMessages().length);
      assertEquals("Notification about forum post", greenMail.getReceivedMessages()[0].getSubject());
      
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic", "forum-watchers" })
  public void testPostNotificationsDisabled() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      setForumNotificationsEmailSetting(4, Boolean.FALSE);
      
      loginInternal("user@foyt.fi", "pass");
      navigate("/forum/1_topic_forum/single_topic");
      assertSelectorVisible(".forum-topic-post-editor-container");
      scrollIntoView(".forum-topic-post-editor-container");
      waitForSelectorVisible(".forum-topic-post-editor-container .cke_wysiwyg_frame");
      setCKEditorContents(0, "<p>Test post</p>");
      scrollWaitAndClick(".forum-topic-post-send-container input[type='submit']"); 
      waitForSelectorCount(".post", 2);
      
      assertEquals(0, greenMail.getReceivedMessages().length);
    } finally {
      greenMail.stop();
    } 
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic", "forum-watchers" })
  public void testPostNotificationsEnabled() throws Exception {
    GreenMail greenMail = startSmtpServer();
    try {
      setForumNotificationsEmailSetting(4, Boolean.TRUE);
      
      loginInternal("user@foyt.fi", "pass");
      navigate("/forum/1_topic_forum/single_topic");
      assertSelectorVisible(".forum-topic-post-editor-container");
      scrollIntoView(".forum-topic-post-editor-container");
      waitForSelectorVisible(".forum-topic-post-editor-container .cke_wysiwyg_frame");
      setCKEditorContents(0, "<p>Test post</p>");
      scrollWaitAndClick(".forum-topic-post-send-container input[type='submit']"); 
      waitForSelectorCount(".post", 2);
      
      assertEquals(1, greenMail.getReceivedMessages().length);
      assertEquals("Notification about forum post", greenMail.getReceivedMessages()[0].getSubject());
    } finally {
      greenMail.stop();
    } 
  }
  
  private void setForumNotificationsEmailSetting(long userId, Boolean value) throws Exception {
    executeSql("insert into UserSetting (userSettingKey, user_id, value) values (?, ?, ?)", 
        UserSettingKey.FORUM_NEW_POST_NOTIFICATION_EMAIL.toString(), userId, value.toString());
  }

}

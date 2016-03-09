package fi.foyt.fni.test.ui.base.forum;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebDriverException;

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
  @DefineSqlSet (id = "forum-with-special-characters", before = { "forum-with-special-characters-setup.sql"}, after={"forum-with-special-characters-teardown.sql" })
})
public class ForumTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTexts() {
    navigateAndWait("/forum/1_topic_forum");
    assertSelectorText(".view-header-description-title", "SINGLE TOPIC FORUM", true, true);
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testAnonymous() {
    navigateAndWait("/forum/1_topic_forum");
    assertSelectorVisible(".forum-new-topic-login-link");
    assertSelectorTextIgnoreCase(".forum-new-topic-login-link", "LOGIN TO CREATE NEW TOPIC");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testLoggedIn() {
    loginInternal("user@foyt.fi", "pass");
    navigateAndWait("/forum/1_topic_forum");
    assertSelectorVisible(".forum-view-new-topic-link");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testNotFound() throws Exception {
    testNotFound("/forum/qwe");
    testNotFound("/forum/*");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "forum-with-special-characters"})
  public void testSpecialCharacter() throws Exception {
    navigateAndWait("/forum/with-special.characters");
    assertTitle("Forum");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "illusion-basic", "event", "event-forum"})
  public void testInvisible() throws Exception {
    testNotFound("/forum/illusion");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testForumLink() {
    navigateAndWait("/forum/1_topic_forum");
    assertSelectorText("h3 a", "Single topic Forum", true, true);
    assertEquals(String.format("%s/forum/1_topic_forum", getAppUrl()), findElementBySelector("h3 a").getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTopicLink() {
    navigateAndWait("/forum/1_topic_forum");
    assertSelectorTextIgnoreCase("*[data-topic-index=\"0\"] h5 a", "Topic of single topic forum");
    assertEquals(String.format("%s/forum/1_topic_forum/single_topic", getAppUrl()), findElementBySelector("*[data-topic-index=\"0\"] h5 a").getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testAuthorLink() {
    navigateAndWait("/forum/");
    assertSelectorTextIgnoreCase("*[data-topic-index=\"0\"] .topic-start-info a", "Test Guest");
    assertEquals(String.format("%s/profile/1", getAppUrl()), findElementBySelector("*[data-topic-index=\"0\"] .topic-start-info a").getAttribute("href"));
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testForumUnread() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    try {
      navigateAndWait("/forum/1_topic_forum");
      waitForSelectorPresent(".topic-post-count");
      assertSelectorText(".topic-post-count span:nth-child(1)", "posts: 1", true, true);
      assertSelectorNotPresent(".topic-post-count span:nth-child(2)");
      assertSelectorNotPresent(".topic-post-count.has-unread");
    } finally {
      logout();
    }
    
    loginInternal("librarian@foyt.fi", "pass");
    try {
      navigate("/forum/1_topic_forum/single_topic");
      waitForSelectorVisible(".cke_wysiwyg_frame");
      executeScript("CKEDITOR.instances[Object.keys(CKEDITOR.instances)[0]].setData('<p>Post</p>')");
      waitAndClick(".forum-topic-post-send-container input");
      takeScreenshot();
    } finally {
      logout();
    }
    
    loginInternal("user@foyt.fi", "pass");
    try {
      navigateAndWait("/forum/1_topic_forum");
      waitForSelectorPresent(".topic-post-count");
      assertSelectorText(".topic-post-count span:nth-child(1)", "posts: 2", true, true);
      assertSelectorText(".topic-post-count span:nth-child(2)", "unread 1", true, true);
      assertSelectorPresent(".topic-post-count.has-unread");
      navigate("/forum/1_topic_forum/single_topic");
      waitForSelectorVisible(".cke_wysiwyg_frame");
      
      navigateAndWait("/forum/1_topic_forum");
      assertSelectorText(".topic-post-count span:nth-child(1)", "posts: 2", true, true);
      assertSelectorNotPresent(".topic-post-count span:nth-child(2)");
      assertSelectorNotPresent(".topic-post-count.has-unread");
    } finally {
      logout();
    }
  }
}

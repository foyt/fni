package fi.foyt.fni.test.ui.base.forum;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import org.junit.Test;

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
})
public class ForumIndexTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTitle() {
    testTitle("/forum/", "Forum");
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic", "illusion-basic", "event", "event-forum"})
  public void testVisibleCategories() throws SQLException, Exception {
    navigate("/forum/");
    assertSelectorCount(".forum", 4);
    assertEquals(5, countForums().intValue());
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testForumLink() {
    navigate("/forum/");
    assertSelectorTextIgnoreCase("*[data-forum-index=\"2\"] h3 a", "Five topic Forum");
    assertEquals(String.format("%s/forum/5_topic_forum", getAppUrl()), findElementBySelector("*[data-forum-index=\"2\"] h3 a").getAttribute("href"));
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testForumMoreLink() {
    navigate("/forum/");
    assertSelectorTextIgnoreCase("*[data-forum-index=\"2\"] .more-link", "More >>");
    assertEquals(String.format("%s/forum/5_topic_forum", getAppUrl()), findElementBySelector("*[data-forum-index=\"2\"] .more-link").getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTopicLink() {
    navigate("/forum/");
    assertSelectorTextIgnoreCase("*[data-forum-index=\"2\"] *[data-topic-index=\"0\"] h5 a", "Topic 5 of 5 topic forum");
    assertEquals(String.format("%s/forum/5_topic_forum/topic5of5", getAppUrl()), findElementBySelector("*[data-forum-index=\"2\"] *[data-topic-index=\"0\"] h5 a").getAttribute("href"));
  }
  
  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testAuthorLink() {
    navigate("/forum/");
    assertSelectorTextIgnoreCase("*[data-forum-index=\"2\"] *[data-topic-index=\"0\"] .topic-start-info a", "Test Guest");
    assertEquals(String.format("%s/profile/1", getAppUrl()), findElementBySelector("*[data-forum-index=\"2\"] *[data-topic-index=\"0\"] .topic-start-info a").getAttribute("href"));
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testForumUnread() throws Exception {
    loginInternal("user@foyt.fi", "pass");
    try {
      navigate("/forum/");
      waitAndAssertSelectorText("*[data-forum-index=\"1\"] .topic-post-count span:nth-child(1)", "posts: 1", true, true);
      assertSelectorNotPresent("*[data-forum-index=\"1\"] .topic-post-count span:nth-child(2)");
      assertSelectorNotPresent("*[data-forum-index=\"1\"] .topic-post-count.has-unread");
    } finally {
      logout();
    }
    
    loginInternal("librarian@foyt.fi", "pass");
    try {
      navigate("/forum/1_topic_forum/single_topic");
      waitForSelectorVisible(".cke_wysiwyg_frame");
      executeScript("CKEDITOR.instances[Object.keys(CKEDITOR.instances)[0]].setData('<p>Post</p>')");
      waitAndClick(".forum-topic-post-send-container input");
      waitForSelectorCount(".post", 2);
    } finally {
      logout();
    }
    
    loginInternal("user@foyt.fi", "pass");
    try {
      navigate("/forum/");
      waitAndAssertSelectorText("*[data-forum-index=\"1\"] .topic-post-count span:nth-child(1)", "posts: 2", true, true);
      assertSelectorText("*[data-forum-index=\"1\"] .topic-post-count span:nth-child(2)", "unread 1", true, true);
      assertSelectorPresent("*[data-forum-index=\"1\"] .topic-post-count.has-unread");
      navigate("/forum/1_topic_forum/single_topic");
      waitForSelectorVisible(".cke_wysiwyg_frame");
      
      navigate("/forum/");
      assertSelectorText("*[data-forum-index=\"1\"] .topic-post-count span:nth-child(1)", "posts: 2", true, true);
      assertSelectorNotPresent("*[data-forum-index=\"1\"] .topic-post-count span:nth-child(2)");
      assertSelectorNotPresent("*[data-forum-index=\"1\"] .topic-post-count.has-unread");
    } finally {
      logout();
    }
  }
}

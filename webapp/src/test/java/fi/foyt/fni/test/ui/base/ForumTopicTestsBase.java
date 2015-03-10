package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({
  @DefineSqlSet (id = "basic-users", before = { "basic-users-setup.sql" }, after = { "basic-users-teardown.sql"  }),
  @DefineSqlSet (id = "forum-basic", before = { "basic-forum-setup.sql" }, after = { "basic-forum-teardown.sql" }),
  @DefineSqlSet (id = "illusion-basic", before = "illusion-basic-setup.sql", after = "illusion-basic-teardown.sql"),
  @DefineSqlSet (id = "event", before = { "illusion-event-open-setup.sql" }, after = { "illusion-event-open-teardown.sql" }),
  @DefineSqlSet (id = "event-forum", before = { "illusion-event-open-forum-setup.sql" }, after = {"illusion-event-open-forum-teardown.sql"}),
  @DefineSqlSet (id = "forum-with-special-characters", before = { "forum-with-special-characters-setup.sql"}, after={"forum-with-special-characters-teardown.sql" })
})
public class ForumTopicTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTexts() {
    getWebDriver().get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
    assertEquals("FIVE TOPIC FORUM", getWebDriver().findElement(By.cssSelector(".view-header-description-title")).getText());
    assertEquals("TOPIC 5 OF 5 TOPIC FORUM", getWebDriver().findElement(By.cssSelector(".forum-topic-panel h3")).getText());
    assertEquals("STARTED AT JAN 1, 2010 BY", getWebDriver().findElement(By.cssSelector(".forum-topic-panel .forum-topic-info-created")).getText());
    assertEquals("TEST GUEST", getWebDriver().findElement(By.cssSelector(".forum-topic-panel a")).getText());
    assertEquals(getAppUrl() + "/profile/1", getWebDriver().findElement(By.cssSelector(".forum-topic-panel a")).getAttribute("href"));
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testAnonymous() {
    getWebDriver().get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-topic-reply-login-container .forum-topic-reply-login-link")).isDisplayed());
    assertEquals("LOGIN TO POST A REPLY", getWebDriver().findElement(By.cssSelector(".forum-topic-reply-login-container .forum-topic-reply-login-link")).getText());
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testLoggedIn() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-topic-panel form")).isDisplayed());
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/forum/5_topic_forum/invalid");
    testNotFound(getWebDriver(), "/forum/5_topic_forum/1234");
    testNotFound(getWebDriver(), "/forum/5_topic_forum/~");
    testNotFound(getWebDriver(), "/forum///asd");
    testNotFound(getWebDriver(), "/forum/*/*");
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

}

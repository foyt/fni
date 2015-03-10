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
public class ForumTestsBase extends AbstractUITest {

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testTexts() {
    getWebDriver().get(getAppUrl() + "/forum/1_topic_forum");
    assertEquals("SINGLE TOPIC FORUM", getWebDriver().findElement(By.cssSelector(".view-header-description-title")).getText());
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testAnonymous() {
    getWebDriver().get(getAppUrl() + "/forum/1_topic_forum");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-new-topic-login-container .forum-new-topic-login-link")).isDisplayed());
    assertEquals("LOGIN TO CREATE NEW TOPIC", getWebDriver().findElement(By.cssSelector(".forum-new-topic-login-container .forum-new-topic-login-link")).getText());
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testLoggedIn() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forum/1_topic_forum");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-view-new-topic-container .forum-view-new-topic-link")).isDisplayed());
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic"})
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/forum/qwe");
    testNotFound(getWebDriver(), "/forum/*");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "forum-with-special-characters"})
  public void testSpecialCharacter() throws Exception {
    navigate("/forum/with-special.characters");
    assertTitle("Forum");
  }

  @Test
  @SqlSets ({"basic-users", "forum-basic", "illusion-basic", "event", "event-forum"})
  public void testInvisible() throws Exception {
    testNotFound("/forum/illusion");
  }


}

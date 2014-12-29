package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.DefineSqlSet;
import fi.foyt.fni.test.DefineSqlSets;
import fi.foyt.fni.test.SqlSets;

@DefineSqlSets({
  @DefineSqlSet (
    id = "forum-basic", 
    before = { "basic-users-setup.sql", "basic-forum-setup.sql"}, 
    after={"basic-forum-teardown.sql", "basic-users-teardown.sql"}
  ),
  @DefineSqlSet (
    id = "forum-special-characters", 
    before = {"basic-users-setup.sql", "basic-forum-setup.sql", "forum-with-special-characters-setup.sql"}, 
    after = {"forum-with-special-characters-teardown.sql", "basic-forum-teardown.sql", "basic-users-teardown.sql"}
  )
})
public class ForumPostEditTestsBase extends AbstractUITest {

  private static final String TEST_POST = "/forum/1_topic_forum/single_topic/edit/11";

  @Test
  @SqlSets ("forum-basic")
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), TEST_POST);
  }

  @Test
  @SqlSets ("forum-basic")
  public void testUser() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), TEST_POST);
  }

  @Test
  @SqlSets ("forum-basic")
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + TEST_POST);
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-edit-post-panel")).isDisplayed());
  }

  @Test
  @SqlSets ("forum-basic")
  public void testNotFound() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testNotFound(getWebDriver(), "/forum/q/single_topic/edit/8");
    testNotFound(getWebDriver(), "/forum/1_topic_forum/q/edit/8");
    testNotFound(getWebDriver(), "/forum//single_topic/edit/8");
    testNotFound(getWebDriver(), "/forum/1_topic_forum//edit/8");
    testNotFound(getWebDriver(), "/forum///edit/8");
    testNotFound(getWebDriver(), "/forum/1_topic_forum/single_topic/edit/abc");
    testNotFound(getWebDriver(), "/forum/1_topic_forum/single_topic/edit/1024");
    testNotFound(getWebDriver(), "/forum/1_topic_forum/single_topic/edit/9");
  }

  @Test
  @SqlSets ("forum-special-characters")
  public void testWithSpecialCharacters() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    navigate("/forum/with-special.characters/with-special.characters/edit/28");
    assertTitle("Edit Reply");
  }

}

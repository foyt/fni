package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import fi.foyt.fni.test.SqlAfter;
import fi.foyt.fni.test.SqlBefore;

public class ForumPostEditTestsBase extends AbstractUITest {

  private static final String TEST_POST = "/forum/1_topic_forum/single_topic/edit/11";

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), TEST_POST);
  }

  @Test
  public void testUser() throws Exception {
    loginInternal(getWebDriver(), "guest@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), TEST_POST);
  }

  @Test
  public void testAdmin() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + TEST_POST);
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-edit-post-panel")).isDisplayed());
  }

  @Test
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
  @SqlBefore ("forum-with-special-characters-setup.sql")
  @SqlAfter ("forum-with-special-characters-teardown.sql")
  public void testWithSpecialCharacters() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    navigate("/forum/with-special.characters/with-special.characters/edit/28");
    assertTitle("Edit Reply");
  }

}

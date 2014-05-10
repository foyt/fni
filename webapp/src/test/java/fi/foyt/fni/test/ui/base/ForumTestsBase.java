package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

public class ForumTestsBase extends AbstractUITest {

  @Test
  public void testTexts() {
    getWebDriver().get(getAppUrl() + "/forum/1_topic_forum");
    assertEquals("SINGLE TOPIC FORUM", getWebDriver().findElement(By.cssSelector(".view-header-description-title")).getText());
  }

  @Test
  public void testAnonymous() {
    getWebDriver().get(getAppUrl() + "/forum/1_topic_forum");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-new-topic-login-container .forum-new-topic-login-link")).isDisplayed());
    assertEquals("LOGIN TO CREATE NEW TOPIC", getWebDriver().findElement(By.cssSelector(".forum-new-topic-login-container .forum-new-topic-login-link")).getText());
  }

  @Test
  public void testLoggedIn() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forum/1_topic_forum");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-view-new-topic-container .forum-view-new-topic-link")).isDisplayed());
  }

  @Test
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/forum/qwe");
    testNotFound(getWebDriver(), "/forum/*");
  }

}

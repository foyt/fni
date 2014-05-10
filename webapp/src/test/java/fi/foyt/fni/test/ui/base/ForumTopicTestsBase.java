package fi.foyt.fni.test.ui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

public class ForumTopicTestsBase extends AbstractUITest {

  @Test
  public void testTexts() {
    getWebDriver().get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
    assertEquals("FIVE TOPIC FORUM", getWebDriver().findElement(By.cssSelector(".view-header-description-title")).getText());
    assertEquals("TOPIC 5 OF 5 TOPIC FORUM", getWebDriver().findElement(By.cssSelector(".forum-topic-panel h3")).getText());
    assertEquals("STARTED AT JAN 1, 2010 BY", getWebDriver().findElement(By.cssSelector(".forum-topic-panel .forum-topic-info-created")).getText());
    assertEquals("TEST GUEST", getWebDriver().findElement(By.cssSelector(".forum-topic-panel a")).getText());
    assertEquals(getAppUrl() + "/profile/1", getWebDriver().findElement(By.cssSelector(".forum-topic-panel a")).getAttribute("href"));
  }

  @Test
  public void testAnonymous() {
    getWebDriver().get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-topic-reply-login-container .forum-topic-reply-login-link")).isDisplayed());
    assertEquals("LOGIN TO POST A REPLY", getWebDriver().findElement(By.cssSelector(".forum-topic-reply-login-container .forum-topic-reply-login-link")).getText());
  }

  @Test
  public void testLoggedIn() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    getWebDriver().get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
    assertTrue(getWebDriver().findElement(By.cssSelector(".forum-topic-panel form")).isDisplayed());
  }

  @Test
  public void testNotFound() throws Exception {
    testNotFound(getWebDriver(), "/forum/5_topic_forum/invalid");
    testNotFound(getWebDriver(), "/forum/5_topic_forum/1234");
    testNotFound(getWebDriver(), "/forum/5_topic_forum/~");
    testNotFound(getWebDriver(), "/forum///asd");
    testNotFound(getWebDriver(), "/forum/*/*");
  }

}

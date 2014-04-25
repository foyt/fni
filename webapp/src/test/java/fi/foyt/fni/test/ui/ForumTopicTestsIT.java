package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class ForumTopicTestsIT extends AbstractUITest {
  
  @Test
  public void testTexts() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/forum/5_topic_forum/topic5of5");
      assertEquals("FIVE TOPIC FORUM", driver.findElement(By.cssSelector(".view-header-description-title")).getText());
      assertEquals("TOPIC 5 OF 5 TOPIC FORUM", driver.findElement(By.cssSelector(".forum-topic-panel h3")).getText());
      assertEquals("STARTED AT JAN 1, 2010 BY", driver.findElement(By.cssSelector(".forum-topic-panel .forum-topic-info-created")).getText());
      assertEquals("TEST GUEST", driver.findElement(By.cssSelector(".forum-topic-panel a")).getText());
      assertEquals(getAppUrl() + "/profile/1", driver.findElement(By.cssSelector(".forum-topic-panel a")).getAttribute("href"));
    } finally {
      driver.close();
    }
  }
  
  public void testAnonymous() {
    ChromeDriver driver = new ChromeDriver();
    try {
      assertTrue(driver.findElement(By.cssSelector("forum-topic-reply-login-container .forum-topic-reply-login-link")).isDisplayed());
      assertEquals("Login to Post a Reply", driver.findElement(By.cssSelector("forum-topic-reply-login-container .forum-topic-reply-login-link")).getText());
    } finally {
      driver.close();
    }
  }
  
  public void testLoggedIn() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      assertTrue(driver.findElement(By.cssSelector("forum-topic-panel form")).isDisplayed());
    } finally {
      driver.close();
    }
      
    
  }
  
}

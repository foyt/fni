package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class ForumTestsIT extends AbstractUITest {
  
  @Test
  public void testTexts() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/forum/1_topic_forum");
      assertEquals("SINGLE TOPIC FORUM", driver.findElement(By.cssSelector(".view-header-description-title")).getText());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAnonymous() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/forum/1_topic_forum");
      assertTrue(driver.findElement(By.cssSelector(".forum-new-topic-login-container .forum-new-topic-login-link")).isDisplayed());
      assertEquals("LOGIN TO CREATE NEW TOPIC", driver.findElement(By.cssSelector(".forum-new-topic-login-container .forum-new-topic-login-link")).getText());
    } finally {
      driver.close();
    }
  }
  
  @Test 
  public void testLoggedIn() {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "user@foyt.fi", "pass");
      driver.get(getAppUrl() + "/forum/1_topic_forum");
      assertTrue(driver.findElement(By.cssSelector(".forum-view-new-topic-container .forum-view-new-topic-link")).isDisplayed());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testNotFound(driver, "/forum/qwe");
      testNotFound(driver, "/forum/*");
    } finally {
      driver.close();
    }
  }
  
}

package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class ForumPostEditTestsIT extends AbstractUITest {
  
  private static final String TEST_POST = "/forum/1_topic_forum/single_topic/edit/8";
  
  @Test
  public void testLoginRedirect() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      testLoginRequired(driver, TEST_POST);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testUser() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "guest@foyt.fi", "pass");
      testAccessDenied(driver, TEST_POST);
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testAdmin() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      driver.get(getAppUrl() + TEST_POST);
      assertTrue(driver.findElement(By.cssSelector(".forum-edit-post-panel")).isDisplayed());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testNotFound() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      loginInternal(driver, "admin@foyt.fi", "pass");
      testNotFound(driver, "/forum/q/single_topic/edit/8");
      testNotFound(driver, "/forum/1_topic_forum/q/edit/8");
      testNotFound(driver, "/forum//single_topic/edit/8");
      testNotFound(driver, "/forum/1_topic_forum//edit/8");
      testNotFound(driver, "/forum///edit/8");
      testNotFound(driver, "/forum/1_topic_forum/single_topic/edit/abc");
      testNotFound(driver, "/forum/1_topic_forum/single_topic/edit/1024");
      testNotFound(driver, "/forum/1_topic_forum/single_topic/edit/9");
    } finally {
      driver.close();
    }
  }

}

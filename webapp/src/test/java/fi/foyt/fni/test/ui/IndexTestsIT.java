package fi.foyt.fni.test.ui;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class IndexTestsIT extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/");
      assertEquals("Forge & Illusion", driver.getTitle());
    } finally {
      driver.close();
    }
  }
  
  @Test
  public void testTexts() {
    ChromeDriver driver = new ChromeDriver();
    try {
      driver.get(getAppUrl() + "/");

      assertEquals("Forge & Illusion is an open platform built for roleplaying and roleplayers.", driver.findElement(By.cssSelector("p.index-description-text")).getText());
      assertEquals("LATEST GAME LIBRARY PUBLICATIONS", driver.findElement(By.cssSelector(".index-publications-panel>h3>a")).getText());
      assertEquals("LATEST FORUM TOPICS", driver.findElement(By.cssSelector(".index-forum-panel>h3>a")).getText());
      assertEquals("NEWS", driver.findElement(By.cssSelector(".index-blog-panel>h3>a")).getText());
      
      assertEquals("More >>", driver.findElement(By.cssSelector(".index-gamelibrary-more")).getText());
      assertEquals("More >>", driver.findElement(By.cssSelector("a.index-forum-more")).getText());
      assertEquals("More >>", driver.findElement(By.cssSelector("a.index-blog-more")).getText());
    } finally {
      driver.close();
    }
  }
  
}

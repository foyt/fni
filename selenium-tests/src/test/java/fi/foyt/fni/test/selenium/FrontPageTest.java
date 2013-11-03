package fi.foyt.fni.test.selenium;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
  
public class FrontPageTest {
  
  private static final String STAGING_URL = "http://fnistaging-foyt.rhcloud.com/";

  @Before
  public void setUp() throws Exception {
    String username = System.getProperty("SAUCE_USERNAME");
    String accessKey = System.getProperty("SAUCE_ACCESS_KEY");
    String travisJobNumber = System.getProperty("TRAVIS_JOB_NUMBER");
    String host = "localhost";
    String port = "4445";

    DesiredCapabilities capabilities = DesiredCapabilities.firefox();
    capabilities.setCapability("version", "17");
    capabilities.setCapability("platform", Platform.XP);
    capabilities.setCapability("tunnel-identifier", travisJobNumber);
    capabilities.setCapability("build", travisJobNumber);
    capabilities.setCapability("general.useragent.ocale", "en-US");
      
    this.driver = new RemoteWebDriver(new URL("http://" + username + ":" + accessKey + "@" + host + ":" + port + "/wd/hub"), capabilities);
  }

  @Test
  public void testFrontPageEn() throws Exception {
    driver.get(STAGING_URL);
    assertEquals("Forge & Illusion", driver.getTitle());

    // Check titles
    assertEquals("Forge & Illusion is a role-playing game oriented environment for game production and playing.", driver.findElement(By.cssSelector("p.index-description-text")).getText());
    assertEquals("Latest Game Library Publications", driver.findElement(By.cssSelector(".index-publications-panel>h3>a")).getText());
    assertEquals("Latest Forum Topics", driver.findElement(By.cssSelector(".index-forum-panel>h3>a")).getText());
    assertEquals("News", driver.findElement(By.cssSelector(".index-blog-panel>h3>a")).getText());
    
    // Check links
    assertEquals("More >>", driver.findElement(By.cssSelector(".index-gamelibrary-more")).getText());
    assertEquals("More >>", driver.findElement(By.cssSelector("a.index-forum-more")).getText());
    assertEquals("More >>", driver.findElement(By.cssSelector("a.index-blog-more")).getText());
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }
  
  private WebDriver driver;
}

package fi.foyt.fni.test.selenium;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.saucelabs.junit.Parallelized;
  
@RunWith(Parallelized.class)
public class FrontPageTest {
  
  private static final String STAGING_URL = "http://fnistaging-foyt.rhcloud.com";

  @Parameterized.Parameters
  public static LinkedList<DesiredCapabilities[]> browsers() throws Exception {
    LinkedList<DesiredCapabilities[]> browsers = new LinkedList<>();
    
    Map<String, Object> extraCapabilities = new HashMap<>();
    
    String travisJobNumber = System.getenv("TRAVIS_JOB_NUMBER");
    if (travisJobNumber != null) {
      extraCapabilities.put("tunnel-identifier", travisJobNumber);
      extraCapabilities.put("build", travisJobNumber);
    }
    extraCapabilities.put("general.useragent.locale", "en-US");
    
    /* Windows 8.1 */
    
    // IE 11
    
    browsers.add(createBrowser(DesiredCapabilities.internetExplorer(), "Windows 8.1", "11", extraCapabilities));

    // Firefox 25
    
    browsers.add(createBrowser(DesiredCapabilities.firefox(), "Windows 8.1", "25", extraCapabilities));

    // Firefox 25
    
    browsers.add(createBrowser(DesiredCapabilities.chrome(), "Windows 8.1", "31", extraCapabilities));

    /* Windows 8 */
    
    // IE 10
    browsers.add(createBrowser(DesiredCapabilities.internetExplorer(), "Windows 8", "10", extraCapabilities));
    
    /* Windows 7 */
    
    // IE 9
    browsers.add(createBrowser(DesiredCapabilities.internetExplorer(), "Windows 7", "9", extraCapabilities));

    // Opera 12
    browsers.add(createBrowser(DesiredCapabilities.internetExplorer(), "Windows 7", "9", extraCapabilities));

    // Safari 5
    browsers.add(createBrowser(DesiredCapabilities.safari(), "Windows 7", "5", extraCapabilities));

    /* Mac */ 
    
    // Safari 6
    
    browsers.add(createBrowser(DesiredCapabilities.safari(), "OS X 10.8", "6", extraCapabilities));
    
    /* Linux */
    
    // Firefox 25
    
    browsers.add(createBrowser(DesiredCapabilities.firefox(), Platform.LINUX, "25", extraCapabilities));

    // Chrome 30
    
    browsers.add(createBrowser(DesiredCapabilities.chrome(), Platform.LINUX, "30", extraCapabilities));
    
    return browsers;
  }
  
  private static DesiredCapabilities[] createBrowser(DesiredCapabilities capabilities, Object platform, String version, Map<String, Object> extraCapabilities) {
    capabilities.setCapability("platform", platform);
    capabilities.setCapability("version", version);
    
    for (String capability : extraCapabilities.keySet()) {
      Object value = extraCapabilities.get(capability);
      capabilities.setCapability(capability, value);
    }
    
    return new DesiredCapabilities[] { capabilities };
  }
  
  public FrontPageTest(DesiredCapabilities capabilities) {
    super();
    
    this.capabilities = capabilities;
  }
  
  @Before
  public void setUp() throws Exception {
    String username = System.getenv("SAUCE_USERNAME");
    String accessKey = System.getenv("SAUCE_ACCESS_KEY");
    String host = System.getenv("SAUCE_HOST"); 
    String port = System.getenv("SAUCE_PORT"); 
    this.driver = new RemoteWebDriver(new URL("http://" + username + ":" + accessKey + "@" + host + ":" + port + "/wd/hub"), capabilities);
  }
  
  @Test
  public void testFrontPageEn() throws Exception {
    driver.get(STAGING_URL);
    assertEquals("Forge & Illusion", driver.getTitle());
    
    // Menu
    
    // Navigation
    
    // TODO: Index
    // TODO: About
    assertEquals("Forge", driver.findElement(By.cssSelector(".index-menu .menu-navigation-container a:nth-child(0)")).getText());
    assertEquals("Game Library", driver.findElement(By.cssSelector(".index-menu .menu-navigation-container a:nth-child(1)")).getText());
    assertEquals("Forum", driver.findElement(By.cssSelector(".index-menu .menu-navigation-container a:nth-child(2)")).getText());
    
    // Check titles
    assertEquals("Forge & Illusion is an open platform built for roleplaying and roleplayers.", driver.findElement(By.cssSelector("p.index-description-text")).getText());
    assertEquals("LATEST GAME LIBRARY PUBLICATIONS", driver.findElement(By.cssSelector(".index-publications-panel>h3>a")).getText());
    assertEquals("LATEST FORUM TOPICS", driver.findElement(By.cssSelector(".index-forum-panel>h3>a")).getText());
    assertEquals("NEWS", driver.findElement(By.cssSelector(".index-blog-panel>h3>a")).getText());
    
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
  private DesiredCapabilities capabilities;
}

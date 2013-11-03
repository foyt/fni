package fi.foyt.fni.test.selenium;

import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
      
    this.driver = new RemoteWebDriver(new URL("http://" + username + ":" + accessKey + "@" + host + ":" + port + "/wd/hub"), capabilities);
  }

  @Test
  public void testFrontPageEn() throws Exception {
    driver.get(STAGING_URL);

//    selenium.addCustomRequestHeader("Accept-Language", "en-US");
//    selenium.waitForPageToLoad("60000");
//    
//    // Check titles
//    assertEquals("Forge & Illusion", selenium.getTitle());
//    assertEquals("Forge & Illusion is a role-playing game oriented environment for game production and playing.", selenium.getText("css=p.index-description-text"));
//    assertEquals("Latest Game Library Publications", selenium.getText("link=Latest Game Library Publications"));
//    assertEquals("Latest Forum Topics", selenium.getText("link=Latest Forum Topics"));
//    assertEquals("News", selenium.getText("link=News"));
//    
//    // Check links
//    assertEquals("More >>", selenium.getText("link=More >>"));
//    assertEquals("More >>", selenium.getText("css=a.index-forum-more"));
//    assertEquals("More >>", selenium.getText("css=a.index-blog-more"));
  }

  @After
  public void tearDown() throws Exception {
    driver.quit();
  }
  
  private WebDriver driver;
}

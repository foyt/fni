package fi.foyt.fni.test.selenium;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class FrontPage {

  @Before
  public void setUp() throws Exception {
    selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://fnistaging-foyt.rhcloud.com/");
    selenium.start();
  }

  @Test
  public void testFrontPageEn() throws Exception {
    selenium.open("/");
    selenium.addCustomRequestHeader("Accept-Language", "en-US");
    selenium.waitForPageToLoad("60000");
    
    // Check titles
    assertEquals("Forge & Illusion", selenium.getTitle());
    assertEquals("Forge & Illusion is a role-playing game oriented environment for game production and playing.", selenium.getText("css=p.index-description-text"));
    assertEquals("Latest Game Library Publications", selenium.getText("link=Latest Game Library Publications"));
    assertEquals("Latest Forum Topics", selenium.getText("link=Latest Forum Topics"));
    assertEquals("News", selenium.getText("link=News"));
    
    // Check links
    assertEquals("More >>", selenium.getText("link=More >>"));
    assertEquals("More >>", selenium.getText("css=a.index-forum-more"));
    assertEquals("More >>", selenium.getText("css=a.index-blog-more"));
  }

  @After
  public void tearDown() throws Exception {
    selenium.stop();
  }

  private Selenium selenium;
}

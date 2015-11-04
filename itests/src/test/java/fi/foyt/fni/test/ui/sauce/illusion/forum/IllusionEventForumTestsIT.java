package fi.foyt.fni.test.ui.sauce.illusion.forum;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventForumTestsBase;

public class IllusionEventForumTestsIT extends IllusionEventForumTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @After
  public void tearDown() {
    getWebDriver().quit();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible"})
  public void testStartWatch() throws MessagingException, IOException {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    if ("safari".equals(getBrowser())) {
      // FIXME: Safari driver does not support typing into ckeditor
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }

    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }
    
    super.testStartWatch();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible"})
  public void testStopWatch() throws MessagingException, IOException {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }

    if ("safari".equals(getBrowser())) {
      // FIXME: Safari driver does not support typing into ckeditor
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }

    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }
    
    super.testStopWatch();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-posts", "event-forum-organizer-posts"})
  public void testPost() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }

    if ("safari".equals(getBrowser())) {
      // FIXME: Safari driver does not support typing into ckeditor
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }

    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }
    
    super.testPost();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-watchers"})
  public void testNotification() throws MessagingException, IOException {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }

    if ("safari".equals(getBrowser())) {
      // FIXME: Safari driver does not support typing into ckeditor
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }

    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Internet Explorer driver does not support typing into ckeditor
      return;
    }
    
    super.testNotification();
  } 
}
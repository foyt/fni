package fi.foyt.fni.test.ui.sauce.illusion;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.illusion.IllusionEventForumTestsBase;
import fi.foyt.fni.test.ui.sauce.SauceLabsUtils;

@RunWith (Parameterized.class)
public class IllusionEventForumTestsIT extends IllusionEventForumTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Parameterized.Parameters
  public static List<String[]> browsers() throws Exception {
    return SauceLabsUtils.getSauceBrowsers();
  }

  public IllusionEventForumTestsIT(String browser, String version, String platform) {
    this.browser = browser;
    this.version = version;
    this.platform = platform;
  }
  
  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver(browser, version, platform));
  }
  
  @After
  public void tearDown() {
    getWebDriver().quit();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible"})
  public void testStartWatch() throws MessagingException, IOException {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.testStartWatch();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible"})
  public void testStopWatch() throws MessagingException, IOException {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.testStopWatch();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-posts", "event-forum-organizer-posts"})
  public void testPost() throws Exception {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }

    super.testPost();
  }
  
  @Override
  @SqlSets({"basic-users", "illusion-basic", "event", "event-participant", "event-organizer", "event-forum", "event-forum-visible", "event-forum-watchers"})
  public void testNotification() throws MessagingException, IOException {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge driver does not support frame switchTo
      return;
    }
    
    super.testNotification();
  }
  
  private String platform;
  private String browser;
  private String version;  
}
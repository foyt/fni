package fi.foyt.fni.test.ui.sauce;

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

import fi.foyt.fni.test.ui.base.IllusionEventForumTestsBase;

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
  public void testStartWatch() throws MessagingException, IOException {
    // TODO: Test disabled because it depends on websocket support (currently not supported by sauce connect)
  }
  
  @Override
  public void testStopWatch() throws MessagingException, IOException {
    // TODO: Test disabled because it depends on websocket support (currently not supported by sauce connect)
  }
  
  @Override
  public void testPost() throws Exception {
    // TODO: Test disabled because it depends on websocket support (currently not supported by sauce connect)
  }
  
  @Override
  public void testNotification() throws MessagingException, IOException {
    // TODO: Test disabled because it depends on websocket support (currently not supported by sauce connect)
  }
  
  private String platform;
  private String browser;
  private String version;  
}
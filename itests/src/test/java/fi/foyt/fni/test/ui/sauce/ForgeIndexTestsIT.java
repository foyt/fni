package fi.foyt.fni.test.ui.sauce;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.ui.base.ForgeIndexTestsBase;

@RunWith (Parameterized.class)
public class ForgeIndexTestsIT extends ForgeIndexTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Parameterized.Parameters
  public static List<String[]> browsers() throws Exception {
    return SauceLabsUtils.getDefaultSauceBrowsers();
  }

  public ForgeIndexTestsIT(String browser, String version, String platform) {
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
  public void testRemoveDialogLongText() {
    if (!"safari".equals(browser)) {
      super.testRemoveDialogLongText();
    } else {
      // FIXME: Test depends on moveToElement, which is not implemented
      // on Safari WebDriver.
    }
  }
  
  private String platform;
  private String browser;
  private String version;  
}
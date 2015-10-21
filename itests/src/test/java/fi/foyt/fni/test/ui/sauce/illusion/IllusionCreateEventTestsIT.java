package fi.foyt.fni.test.ui.sauce.illusion;

import java.net.MalformedURLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.illusion.IllusionCreateEventTestsBase;
import fi.foyt.fni.test.ui.sauce.SauceLabsUtils;

@RunWith (Parameterized.class)
public class IllusionCreateEventTestsIT extends IllusionCreateEventTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Parameterized.Parameters
  public static List<String[]> browsers() throws Exception {
    return SauceLabsUtils.getSauceBrowsers();
  }

  public IllusionCreateEventTestsIT(String browser, String version, String platform) {
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
  @SqlSets ("illusion-basic")
  public void testCreateEventType() throws Exception {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Microsoft edge driver does not support checking checkboxes
      return;
    }
    
    super.testCreateEventType();
  }
  
  @Override
  @SqlSets ("illusion-basic")
  public void testEventLarpKalenteriCreateLocation() throws Exception {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Test does not work on edge, needs more investigation
      return;
    }
    
    super.testEventLarpKalenteriCreateLocation();
  }
  
  private String platform;
  private String browser;
  private String version;  
}
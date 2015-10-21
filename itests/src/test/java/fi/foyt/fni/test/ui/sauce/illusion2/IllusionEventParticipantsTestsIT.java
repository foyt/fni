package fi.foyt.fni.test.ui.sauce.illusion2;

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
import fi.foyt.fni.test.ui.base.illusion.IllusionEventParticipantsTestsBase;
import fi.foyt.fni.test.ui.sauce.SauceLabsUtils;

@RunWith (Parameterized.class)
public class IllusionEventParticipantsTestsIT extends IllusionEventParticipantsTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Parameterized.Parameters
  public static List<String[]> browsers() throws Exception {
    return SauceLabsUtils.getSauceBrowsers();
  }

  public IllusionEventParticipantsTestsIT(String browser, String version, String platform) {
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
  @SqlSets ({"illusion-basic", "illusion-event", "illusion-event-participant", "illusion-event-organizer"})
  public void testUpdateRole() {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Microsoft edge driver does not support checking checkboxes
      return;
    }
    
    super.testUpdateRole();
  }
  
  private String platform;
  private String browser;
  private String version;  
}
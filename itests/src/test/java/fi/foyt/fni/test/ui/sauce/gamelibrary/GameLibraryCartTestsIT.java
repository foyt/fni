package fi.foyt.fni.test.ui.sauce.gamelibrary;

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
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryCartTestsBase;
import fi.foyt.fni.test.ui.sauce.SauceLabsUtils;

@RunWith (Parameterized.class)
public class GameLibraryCartTestsIT extends GameLibraryCartTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Parameterized.Parameters
  public static List<String[]> browsers() throws Exception {
    return SauceLabsUtils.getSauceBrowsers();
  }

  public GameLibraryCartTestsIT(String browser, String version, String platform) {
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
  @SqlSets ("basic-gamelibrary")
  public void testMultiItemPurchase() throws Exception {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge driver has some trouble handling unicode characters in sendKeys requests
      return;
    }
  
    super.testMultiItemPurchase();
  }
  
  @Override
  @SqlSets ("basic-gamelibrary")
  public void testCartLoggedIn() throws Exception {
    if ("microsoftedge".equals(browser)) {
      // FIXME: Edge driver has some trouble handling unicode characters in sendKeys requests
      return;
    }
    
    super.testCartLoggedIn();
  }
  
  private String platform;
  private String browser;
  private String version;  
}
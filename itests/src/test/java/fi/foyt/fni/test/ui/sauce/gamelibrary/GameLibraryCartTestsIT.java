package fi.foyt.fni.test.ui.sauce.gamelibrary;

import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryCartTestsBase;

public class GameLibraryCartTestsIT extends GameLibraryCartTestsBase {

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
  @SqlSets ("basic-gamelibrary")
  public void testMultiItemPurchase() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver has some trouble handling unicode characters in sendKeys requests
      return;
    }
  
    super.testMultiItemPurchase();
  }
  
  @Override
  @SqlSets ("basic-gamelibrary")
  public void testCartLoggedIn() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: Edge driver has some trouble handling unicode characters in sendKeys requests
      return;
    }
    
    super.testCartLoggedIn();
  }
 
}
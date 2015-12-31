package fi.foyt.fni.test.ui.sauce.gamelibrary;

import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.SqlSets;
import fi.foyt.fni.test.ui.base.gamelibrary.GameLibraryProposeGameTestsBase;

public class GameLibraryProposeGameTestsIT extends GameLibraryProposeGameTestsBase {

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
  @SqlSets ("basic-users")
  public void testPropose() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("safari".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("firefox".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    super.testPropose();
  }
  
  @Override
  @SqlSets ("basic-users")
  public void testProposeLicenseCC() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("safari".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("firefox".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    super.testProposeLicenseCC();
  }
  
  @Override
  @SqlSets ("basic-users")
  public void testProposeLicenseOther() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("safari".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("firefox".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    super.testProposeLicenseOther();
  }
  
  @Override
  @SqlSets ("basic-users")
  public void testProposeTags() throws Exception {
    if ("microsoftedge".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("safari".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    if ("firefox".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }

    if ("internet explorer".equals(getBrowser())) {
      // FIXME: File upload does not work with this driver
      return;
    }
    
    super.testProposeTags();
  }
  
}
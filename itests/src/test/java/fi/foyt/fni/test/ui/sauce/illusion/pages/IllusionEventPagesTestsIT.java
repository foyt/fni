package fi.foyt.fni.test.ui.sauce.illusion.pages;

import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.ui.base.illusion.IllusionEventPagesTestsBase;

public class IllusionEventPagesTestsIT extends IllusionEventPagesTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);

  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
  @Override
  public void testPagePermaLink() throws Exception {
    // TODO: Re-enable test. Currently disabled because test depends on 
    // websockets and sauce connect does not support them
  }
  
  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}
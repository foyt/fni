package fi.foyt.fni.test.ui.sauce.forum;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Rule;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

import fi.foyt.fni.test.ui.base.forum.ForumTestsBase;

public class ForumTestsIT extends ForumTestsBase {

  public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(getSauceUsername(), getSauceAccessKey());

  @Rule
  public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(this, authentication);
  
  @Before
  public void setUp() throws MalformedURLException {
    setWebDriver(createSauceWebDriver());
  }
  
}
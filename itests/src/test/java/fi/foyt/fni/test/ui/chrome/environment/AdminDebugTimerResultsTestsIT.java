package fi.foyt.fni.test.ui.chrome.environment;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.AdminDebugTimerResultsTestsBase;

public class AdminDebugTimerResultsTestsIT extends AdminDebugTimerResultsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }

}

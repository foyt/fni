package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.AdminDebugTimerResultsTestsBase;

public class AdminDebugTimerResultsTestsIT extends AdminDebugTimerResultsTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}

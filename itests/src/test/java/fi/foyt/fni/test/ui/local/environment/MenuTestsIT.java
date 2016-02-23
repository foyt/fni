package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.MenuTestsBase;

public class MenuTestsIT extends MenuTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}
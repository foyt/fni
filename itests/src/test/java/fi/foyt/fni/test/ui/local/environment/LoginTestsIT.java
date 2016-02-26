package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.LoginTestsBase;

public class LoginTestsIT extends LoginTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}
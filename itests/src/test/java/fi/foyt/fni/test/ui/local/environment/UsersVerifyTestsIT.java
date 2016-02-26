package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.UsersVerifyTestsBase;

public class UsersVerifyTestsIT extends UsersVerifyTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}
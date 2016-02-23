package fi.foyt.fni.test.ui.local.environment;

import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.UsersResetPasswordTestsBase;

public class UsersResetPasswordTestsIT extends UsersResetPasswordTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createLocalDriver());
  }

}
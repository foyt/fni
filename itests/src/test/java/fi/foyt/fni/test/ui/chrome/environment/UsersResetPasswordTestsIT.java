package fi.foyt.fni.test.ui.chrome.environment;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.UsersResetPasswordTestsBase;

public class UsersResetPasswordTestsIT extends UsersResetPasswordTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}
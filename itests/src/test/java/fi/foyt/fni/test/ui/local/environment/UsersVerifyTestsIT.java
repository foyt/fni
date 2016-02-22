package fi.foyt.fni.test.ui.local.environment;

import org.junit.After;
import org.junit.Before;
import fi.foyt.fni.test.ui.base.environment.UsersVerifyTestsBase;

public class UsersVerifyTestsIT extends UsersVerifyTestsBase {

  @Before
  public void setUp() {
    setWebDriver(createChromeDriver());
  }

  @After
  public void tearDown() {
    getWebDriver().quit();
  }
   
}
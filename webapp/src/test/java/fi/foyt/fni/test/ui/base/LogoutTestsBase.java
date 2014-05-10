package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class LogoutTestsBase extends AbstractUITest {

  @Test
  public void testTitle() {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    logout(getWebDriver());
  }

}

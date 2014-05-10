package fi.foyt.fni.test.ui.base;

import org.junit.Test;

public class AdminDebugTimerResultsTestsBase extends AbstractUITest {

  @Test
  public void testTitle() throws Exception {
    loginInternal(getWebDriver(), "admin@foyt.fi", "pass");
    testTitle(getWebDriver(), "/admin/debug-timer-results", "Debug Timer Results");
  }

  @Test
  public void testLoginRedirect() throws Exception {
    testLoginRequired(getWebDriver(), "/admin/debug-timer-results");
  }

  @Test
  public void testUnauthorized() throws Exception {
    loginInternal(getWebDriver(), "user@foyt.fi", "pass");
    testAccessDenied(getWebDriver(), "/admin/debug-timer-results");
  }

}
